package ch.crb.showcase.animals.entity;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.stream.JsonCollectors;
import javax.persistence.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table
@NamedQueries({
        @NamedQuery(name = Animal.FIND_ALL, query = "SELECT a FROM Animal a")
})
public class Animal {
    public static final String FIND_ALL = "Animal.FindAll";

    public static final class JSON_FIELD_NAMES {
        public static final String ID = "id";
        public static final String NAMES = "names";
        public static final String CHIP_ID = "chipId";
        public static final String IMAGE_PATHS = "imagePaths";
        public static final String BIRTHDAY = "birthday";
        public static final String ACQUIRED = "acquired";
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "animal")
    public List<AnimalName> names = new ArrayList<>();
    @Column(nullable = false)
    public String chipId;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "animal")
    public List<AnimalImage> imagePaths;
    @Column
    public LocalDate birthday;
    @Column
    public LocalDate acquired;


    public Animal() {
    }

    public Animal(JsonObject json) {
        try {
            this.id = json.getJsonNumber(JSON_FIELD_NAMES.ID).longValue();
        } catch (Exception e) {
            // ID not set no problemo
        }
        this.chipId = json.getString(JSON_FIELD_NAMES.CHIP_ID, this.chipId);
        this.birthday = LocalDate.parse(json.getString(JSON_FIELD_NAMES.BIRTHDAY), DateTimeFormatter.ISO_DATE);
        this.acquired = LocalDate.parse(json.getString(JSON_FIELD_NAMES.ACQUIRED), DateTimeFormatter.ISO_DATE);
    }

    public JsonObject toOverviewJson() {
        return getSimpleJsonBuilder()
                .add(JSON_FIELD_NAMES.IMAGE_PATHS, this.imagePaths
                        .stream()
                        .map(AnimalImage::toOverviewJson)
                        .collect(JsonCollectors.toJsonArray())
                )
                .build();
    }

    public JsonObjectBuilder getSimpleJsonBuilder() {
        return Json.createObjectBuilder()
                .add(JSON_FIELD_NAMES.ID, this.id)
                .add(JSON_FIELD_NAMES.NAMES, this.names
                        .stream()
                        .map(AnimalName::toOverviewJson)
                        .collect(JsonCollectors.toJsonArray())
                )
                .add(JSON_FIELD_NAMES.BIRTHDAY, DateTimeFormatter.ISO_DATE.format(this.birthday))
                .add(JSON_FIELD_NAMES.ACQUIRED, DateTimeFormatter.ISO_DATE.format(this.acquired))
                .add(JSON_FIELD_NAMES.CHIP_ID, this.chipId);
    }

}


