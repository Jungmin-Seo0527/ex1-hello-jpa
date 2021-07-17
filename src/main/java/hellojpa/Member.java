package hellojpa;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.TableGenerator;
import java.util.ArrayList;
import java.util.List;

@Entity
// @SequenceGenerator(name = "member_seq_generator",
//         sequenceName = "member_seq", // 매핑할 데이터베이스 시퀀스 이름
//         initialValue = 1, allocationSize = 1)
@TableGenerator(
        name = "member_seq_generator",
        table = "MY_SEQUENCES",
        pkColumnValue = "MEMBER_SEQ", allocationSize = 1
)
public class Member extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "member_seq_generator")
    private Long id;

    @Column(name = "name")
    private String username;

    @OneToMany(mappedBy = "member")
    private List<MemberProduct> memberProducts = new ArrayList<>();


    public Member() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
