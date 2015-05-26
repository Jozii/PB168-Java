package PropertyManager.manager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Calendar;

/**
 * Created by Jozef Živčic on 10. 3. 2015.
 */
public class Property {
    private Long id;
    private String addressStreet;
    private String addressTown;
    private BigDecimal price;
    private String typeOfBuilding;
    private int squareMeters;
    private LocalDate dateOfBuild;
    private String description;

    public Property() {

    }
    public Property(Long id, String addressStreet, String addressTown, BigDecimal price, String type, int squareMeters, LocalDate dateOfBuild, String description) {
        this.id = id;
        this.addressStreet = addressStreet;
        this.addressTown = addressTown;
        this.price = price;
        this.typeOfBuilding = type;
        this.squareMeters = squareMeters;
        this.dateOfBuild = dateOfBuild;
        this.description = description;
    }

    public Property(String addressStreet, String addressTown, BigDecimal price, String type, int squareMeters, LocalDate dateOfBuild, String description) {
        this.addressStreet = addressStreet;
        this.addressTown = addressTown;
        this.price = price;
        this.typeOfBuilding = type;
        this.squareMeters = squareMeters;
        this.dateOfBuild = dateOfBuild;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public String getAddressTown() {
        return addressTown;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getType() {
        return typeOfBuilding;
    }

    public int getSquareMeters() {
        return squareMeters;
    }

    public LocalDate getDateOfBuild() {
        return dateOfBuild;
    }

    public String getDescription() {
        return description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public void setAddressTown(String addressTown) {
        this.addressTown = addressTown;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setType(String type) {
        this.typeOfBuilding = type;
    }

    public void setSquareMeters(int squareMeters) {
        this.squareMeters = squareMeters;
    }

    public void setDateOfBuild(LocalDate dateOfBuild) {
        this.dateOfBuild = dateOfBuild;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Property property = (Property) o;

        if (!id.equals(property.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
    
    @Override
    public String toString() {
        return addressStreet + ",  " + addressTown;
    }
}
