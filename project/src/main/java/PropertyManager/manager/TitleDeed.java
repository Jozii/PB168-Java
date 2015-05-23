package PropertyManager.manager;

import java.time.LocalDate;
import java.util.Calendar;

/**
 * Created by Jozef Živčic on 10. 3. 2015.
 */
public class TitleDeed {
    private Long id;
    private Long ownerId;
    private Long propertyId;
    private LocalDate startDate;
    private LocalDate endDate;

    public TitleDeed() {

    }

    public TitleDeed(Long owner, Long property, LocalDate startDate, LocalDate endDate) {
        this.ownerId = owner;
        this.propertyId = property;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public TitleDeed(Long id, Long owner, Long property, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.ownerId = owner;
        this.propertyId = property;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public Long getOwner() {
        return ownerId;
    }

    public Long getProperty() {
        return propertyId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setOwner(Long owner) {
        this.ownerId = owner;
    }

    public void setProperty(Long property) {
        this.propertyId = property;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TitleDeed titleDeed = (TitleDeed) o;

        if (endDate != null ? !endDate.equals(titleDeed.endDate) : titleDeed.endDate != null) return false;
        if (id != null ? !id.equals(titleDeed.id) : titleDeed.id != null) return false;
        if (ownerId != null ? !ownerId.equals(titleDeed.ownerId) : titleDeed.ownerId != null) return false;
        if (propertyId != null ? !propertyId.equals(titleDeed.propertyId) : titleDeed.propertyId != null) return false;
        if (startDate != null ? !startDate.equals(titleDeed.startDate) : titleDeed.startDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (ownerId != null ? ownerId.hashCode() : 0);
        result = 31 * result + (propertyId != null ? propertyId.hashCode() : 0);
        result = 31 * result + (startDate != null ? startDate.hashCode() : 0);
        result = 31 * result + (endDate != null ? endDate.hashCode() : 0);
        return result;
    }

}
