package ru.yandex.practicum.filmorate.review.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.filmorate.film.model.Film;
import ru.yandex.practicum.filmorate.user.model.User;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "is_positive")
    private Boolean positive;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "film_id")
    private Film film;

    @Column(name = "useful")
    private Long useful;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ReviewGrade> grades = new HashSet<>();

    public void addGrade(ReviewGrade grade) {
        grade.setReview(this);

        this.grades.add(grade);

        if (grade.getIsPositive() == true) {
            this.setUseful(this.getUseful() + 1);
        } else {
            this.setUseful(this.getUseful() - 1);
        }
    }

    public void removeGrade(ReviewGrade grade) {
        this.grades.remove(grade);

        boolean isPositive = grade.getIsPositive() == true;
        boolean isNotZero = this.getUseful() != 0;

        if (isPositive && isNotZero) {
            this.setUseful(this.getUseful() - 1);
        } else {
            this.setUseful(this.getUseful() + 1);
        }
    }

    public Optional<ReviewGrade> getGradeByUserAndPositiveType(User user, boolean isPositive) {
        for (ReviewGrade reviewGrade : this.grades) {

            var gradeUserId = reviewGrade.getUser().getId();
            var userId = user.getId();

            var isSameUser = Objects.equals(gradeUserId, userId);
            var isSameGradeType = reviewGrade.getIsPositive() == isPositive;

            if (isSameUser && isSameGradeType) {
                return Optional.of(reviewGrade);
            }
        }

        return Optional.empty();
    }
}
