package PropertyManager.manager;


import java.time.LocalDate;

/**
 * Created by Jozef Živčic on 10. 3. 2015.
 */
public class Owner {
    private Long id;
    private String name;
    private String surname;
    private LocalDate born;
    private String phoneNumber;
    private String addressStreet;
    private String addressTown;

    public Owner() {
    }

    public Owner(String name, String surname, LocalDate born, String phoneNumber, String addressStreet, String addressTown) {
        this.id = null;
        this.name = name;
        this.surname = surname;
        this.born = born;
        this.phoneNumber = phoneNumber;
        this.addressStreet = addressStreet;
        this.addressTown = addressTown;
    }

    public Owner(Long id, String name, String surname, LocalDate born, String phoneNumber, String addressStreet, String addressTown) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.born = born;
        this.phoneNumber = phoneNumber;
        this.addressStreet = addressStreet;
        this.addressTown = addressTown;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public LocalDate getBorn() {
        return born;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getAddressStreet() {
        return addressStreet;
    }

    public String getAddressTown() {
        return addressTown;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setBorn(LocalDate born) {
        this.born = born;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setAddressStreet(String addressStreet) {
        this.addressStreet = addressStreet;
    }

    public void setAddressTown(String addressTown) {
        this.addressTown = addressTown;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Owner owner = (Owner) o;

        if (addressStreet != null ? !addressStreet.equals(owner.addressStreet) : owner.addressStreet != null)
            return false;
        if (addressTown != null ? !addressTown.equals(owner.addressTown) : owner.addressTown != null) return false;
        if (born != null ? !born.equals(owner.born) : owner.born != null) return false;
        if (id != null ? !id.equals(owner.id) : owner.id != null) return false;
        if (name != null ? !name.equals(owner.name) : owner.name != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(owner.phoneNumber) : owner.phoneNumber != null) return false;
        if (surname != null ? !surname.equals(owner.surname) : owner.surname != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (surname != null ? surname.hashCode() : 0);
        result = 31 * result + (born != null ? born.hashCode() : 0);
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (addressStreet != null ? addressStreet.hashCode() : 0);
        result = 31 * result + (addressTown != null ? addressTown.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return id + ". " + name + " " + surname;
    }
}
