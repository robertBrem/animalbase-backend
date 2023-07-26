package ch.crb.showcase.animals.entity;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.persistence.*;

@Entity
@Table
public class AnimalName {
    public static final class JSON_FIELD_NAMES {
        public static final String ID = "id";
        public static final String ANIMAL_ID = "animalId";
        public static final String LANGUAGE = "language";
        public static final String TRANSLATION = "translation";
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    public Animal animal;
    public String language;
    public String translation;
    public AnimalName() {
    }

    public AnimalName(JsonObject json) {
        try {
            this.id = json.getJsonNumber(AnimalName.JSON_FIELD_NAMES.ID).longValue();
        } catch (Exception e) {
            // ID not set no problemo
        }
        this.language = json.getString(JSON_FIELD_NAMES.LANGUAGE, this.language);
        this.translation = json.getString(JSON_FIELD_NAMES.TRANSLATION, this.translation);
    }
    public JsonObject toOverviewJson() {
        return getSimpleJsonBuilder()
                .build();
    }

    public JsonObjectBuilder getSimpleJsonBuilder() {
        return Json.createObjectBuilder()
                .add(AnimalName.JSON_FIELD_NAMES.ID, this.id)
                .add(AnimalName.JSON_FIELD_NAMES.ANIMAL_ID, this.animal.id)
                .add(AnimalName.JSON_FIELD_NAMES.LANGUAGE, this.language)
                .add(JSON_FIELD_NAMES.TRANSLATION, this.translation);
    }
}
