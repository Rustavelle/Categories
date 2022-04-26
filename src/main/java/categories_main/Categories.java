package categories_main;

import javax.persistence.*;

@Entity
@Table(name = "categories")
public class Categories {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "category_name")
    private String name;

    @Column(name = "left_key")
    private int LeftKey;

    @Column (name = "right_key")
    private int RightKey;

    @Column(name = "level_category")
    private int LevelCategory;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getLeftKey() {
        return LeftKey;
    }

    public void setLeftKey(int leftKey) {
        LeftKey = leftKey;
    }

    public int getRightKey() {
        return RightKey;
    }

    public void setRightKey(int rightKey) {
        RightKey = rightKey;
    }

    public int getLevelCategory() {
        return LevelCategory;
    }

    public void setLevelCategory(int levelCategory) {
        LevelCategory = levelCategory;
    }
}
