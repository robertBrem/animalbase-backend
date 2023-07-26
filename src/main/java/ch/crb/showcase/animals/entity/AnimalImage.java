package ch.crb.showcase.animals.entity;

import jdk.vm.ci.meta.Local;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Entity
@Table
public class AnimalImage {
    public static final class JSON_FIELD_NAMES {
        public static final String ID = "id";
        public static final String ANIMAL_ID = "animalId";
        public static final String PATH = "path";
        public static final String UPLOAD_DATE = "uploadDate";
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    public Animal animal;
    public String path;
    public LocalDate uploadDate;
    public AnimalImage() {
    }

    public AnimalImage(JsonObject json) {
        try {
            this.id = json.getJsonNumber(AnimalImage.JSON_FIELD_NAMES.ID).longValue();
        } catch (Exception e) {
            // ID not set no problemo
        }
        this.path = json.getString(JSON_FIELD_NAMES.PATH, this.path);
        this.uploadDate = LocalDate.now();
    }

    public JsonObject toOverviewJson() {
        return getSimpleJsonBuilder()
                .build();
    }

    public JsonObjectBuilder getSimpleJsonBuilder() {
        return Json.createObjectBuilder()
                .add(AnimalImage.JSON_FIELD_NAMES.ID, this.id)
                .add(AnimalImage.JSON_FIELD_NAMES.ANIMAL_ID, this.animal.id)
                .add(JSON_FIELD_NAMES.PATH, this.path)
                .add(JSON_FIELD_NAMES.UPLOAD_DATE, DateTimeFormatter.ISO_DATE.format(this.uploadDate));
    }
}
