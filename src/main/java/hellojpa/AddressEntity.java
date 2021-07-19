package hellojpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ADDRESS")
public class AddressEntity {

    @Id @GeneratedValue
    private Long id;

    private Address address;

    public AddressEntity(String homeCity, String street, String s) {
        this.address = new Address(homeCity, street, s);
    }

    public AddressEntity(Address address) {
        this.address = address;
    }
}
