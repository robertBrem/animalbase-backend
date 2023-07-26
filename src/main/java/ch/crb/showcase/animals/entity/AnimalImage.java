package ch.crb.showcase.animals.entity;

import javax.persistence.*;

@Entity
@Table
public class AnimalImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    public Animal animal;
    public String path;
}
