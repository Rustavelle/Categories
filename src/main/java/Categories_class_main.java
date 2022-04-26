import categories_main.Categories;

import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class Categories_class_main {

    public static Scanner scanner = new Scanner(System.in);

    public static EntityManagerFactory factory = Persistence. createEntityManagerFactory("main");

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберете действие:\n 1)Создание \n 2)Перемещение \n 3)Удаление");
        String actionId = scanner.nextLine();
        switch (actionId) {
            case "1" -> create();
            case "2" -> moving();
            case "3" -> delete();
        }
    }

    private static void create(){

        EntityManager manager = factory.createEntityManager();
        try {
            TypedQuery<Categories> categoriesTypedQuery = manager.createQuery
                    ("select c from Categories c", Categories.class);
            List<Categories> categoriesList = categoriesTypedQuery.getResultList();
            for (Categories categories : categoriesList) {
                System.out.println(categories.getId() + " - "
                        + categories.getLeftKey() + "-" + categories.getRightKey() + " - " + categories.getName());
            }

            System.out.println("Выберете id для добавления категории, либо 0 для создания новой категории: ");
            String categoryName = scanner.nextLine();
            manager.getTransaction().begin();

            if (Long.parseLong(categoryName) == 0) {
                TypedQuery<Integer> maxRightKeyQuery = manager.createQuery
                        ("select max(c.RightKey) from Categories c", Integer.class);
                List<Integer> maxRightKeyQueryList = maxRightKeyQuery.getResultList();
                Categories newCategory = new Categories();
                newCategory.setLeftKey(maxRightKeyQueryList.get(0) + 1);
                newCategory.setRightKey(maxRightKeyQueryList.get(0) + 2);
                newCategory.setLevelCategory(0);
                System.out.println("Введите название новой категории: ");
                String NewNameCategory = scanner.nextLine();
                newCategory.setName(NewNameCategory);
                manager.persist(newCategory);

            } else if (Long.parseLong(categoryName) != 0) {

                Categories categories = manager.find(Categories.class, Long.parseLong(categoryName));

                Query updateRightKey = manager.createQuery("update Categories set RightKey = RightKey + 2 where RightKey >= ?1");
                updateRightKey.setParameter(1, categories.getRightKey());
                updateRightKey.executeUpdate();

                Query updateLeftKey = manager.createQuery("update Categories set LeftKey = LeftKey + 2 where LeftKey > ?1");
                updateLeftKey.setParameter(1, categories.getRightKey());
                updateLeftKey.executeUpdate();

                Categories category = new Categories();
                category.setLeftKey(categories.getRightKey());
                category.setRightKey(categories.getRightKey() + 1);
                category.setLevelCategory(categories.getLevelCategory() + 1);

                System.out.println("Введите название новой категории: ");
                String NewCategory = scanner.nextLine();
                category.setName(NewCategory);
                manager.persist(category);
            }
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    private static void moving(){

        EntityManager manager = factory.createEntityManager();

        try {
            manager.getTransaction().begin();
            TypedQuery<Categories> categoriesTypedQuery = manager.createQuery
                    ("select c from Categories c", Categories.class);
            List<Categories> categoriesList = categoriesTypedQuery.getResultList();
            for (Categories categories : categoriesList) {
                System.out.println(categories.getId() + " - "
                        + categories.getLeftKey() + "-" + categories.getRightKey() + " - " + categories.getName());
            }

            System.out.println("Выберете категорию что перенести : ");
            String category = scanner.nextLine();
            Categories categories1 = manager.find(Categories.class, Long.parseLong(category));
            System.out.println(categories1.getId() + " - "
                    + categories1.getLeftKey() + "-" + categories1.getRightKey() + " - " + categories1.getName());

            System.out.println("Выберете категорию куда перенести :");
            String categorySetId = scanner.nextLine();

            if (Long.parseLong(categorySetId) == 0) {
                Query updateKeys = manager.createQuery("update Categories set " +
                        "LeftKey = LeftKey * -1, RightKey = RightKey * -1 where LeftKey >= ?1 and RightKey <= ?2");
                updateKeys.setParameter(1, categories1.getLeftKey());
                updateKeys.setParameter(2, categories1.getRightKey());
                updateKeys.executeUpdate();

                int moveSize = categories1.getRightKey() - categories1.getLeftKey() + 1;
                Query updateRightKey = manager.createQuery
                        ("update Categories set RightKey = RightKey - ?1 where RightKey >= ?2");
                updateRightKey.setParameter(1, moveSize);
                updateRightKey.setParameter(2, categories1.getRightKey());
                updateRightKey.executeUpdate();

                Query updateLeftKey = manager.createQuery("update Categories set LeftKey = LeftKey - ?1 where LeftKey > ?2");
                updateLeftKey.setParameter(1, moveSize);
                updateLeftKey.setParameter(2, categories1.getRightKey());
                updateLeftKey.executeUpdate();

                TypedQuery<Integer> maxRightKeyQuery = manager.createQuery
                        ("select max(c.RightKey) from Categories c", Integer.class);
                List<Integer> maxRightKeyQueryList = maxRightKeyQuery.getResultList();
                int maxRightKey = maxRightKeyQueryList.get(0);

                Query maxRightKeysQuery = manager.createQuery("update Categories set " +
                        "LeftKey = 0 - LeftKey - ?1 + ?2 + 1, RightKey = 0 - RightKey - ?1 + ?2 + 1," +
                        " LevelCategory = LevelCategory - ?3  where LeftKey < 0 and RightKey < 0");
                maxRightKeysQuery.setParameter(1, categories1.getLeftKey());
                maxRightKeysQuery.setParameter(2, maxRightKey);
                maxRightKeysQuery.setParameter(3, categories1.getLevelCategory());
                maxRightKeysQuery.executeUpdate();

                manager.getTransaction().commit();

            } else if (Long.parseLong(categorySetId) != 0) {
                Query updateKeys = manager.createQuery("update Categories set " +
                        "LeftKey = LeftKey * -1, RightKey = RightKey * -1 where LeftKey >= ?1 and RightKey <= ?2");
                updateKeys.setParameter(1, categories1.getLeftKey());
                updateKeys.setParameter(2, categories1.getRightKey());
                updateKeys.executeUpdate();

                int moveSize = categories1.getRightKey() - categories1.getLeftKey() + 1;
                Query updateRightKey = manager.createQuery
                        ("update Categories set RightKey = RightKey - ?1 where RightKey >= ?2");
                updateRightKey.setParameter(1, moveSize);
                updateRightKey.setParameter(2, categories1.getRightKey());
                updateRightKey.executeUpdate();

                Query updateLeftKey = manager.createQuery("update Categories set LeftKey = LeftKey - ?1 where LeftKey > ?2");
                updateLeftKey.setParameter(1, moveSize);
                updateLeftKey.setParameter(2, categories1.getRightKey());
                updateLeftKey.executeUpdate();

                Categories NewParent = manager.find(Categories.class, Long.parseLong(categorySetId));
                manager.refresh(NewParent);
                System.out.println(NewParent.getId() + " - "
                        + NewParent.getLeftKey() + "-" + NewParent.getRightKey() + " - " + NewParent.getName());
                Query updateNewRKey = manager.createQuery("update Categories set RightKey = RightKey + ?1 where RightKey >= ?2");
                updateNewRKey.setParameter(1, moveSize);
                updateNewRKey.setParameter(2, NewParent.getRightKey());
                updateNewRKey.executeUpdate();

                Query updateNewLKey = manager.createQuery("update Categories set LeftKey = LeftKey + ?1 where LeftKey > ?2");
                updateNewLKey.setParameter(1, moveSize);
                updateNewLKey.setParameter(2, NewParent.getRightKey());
                updateNewLKey.executeUpdate();

                manager.refresh(NewParent);

                int newRightKey = NewParent.getRightKey() - categories1.getRightKey() - 1;
                int newLevel = NewParent.getLevelCategory() - categories1.getLevelCategory() + 1;

                Query newKeys = manager.createQuery
                        ("update Categories set LeftKey = 0 - LeftKey + ?1, RightKey = 0 - RightKey + ?1, " +
                                "LevelCategory = LevelCategory + ?2 where LeftKey < 0 and RightKey < 0");
                newKeys.setParameter(1, newRightKey);
                newKeys.setParameter(2, newLevel);
                newKeys.executeUpdate();
                manager.getTransaction().commit();
            }
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    private static void delete(){

        EntityManager manager = factory.createEntityManager();

        try {
            TypedQuery<Categories> categoriesTypedQuery = manager.createQuery
                    ("select c from Categories c", Categories.class);
            List<Categories> categoriesList = categoriesTypedQuery.getResultList();
            for (Categories categories : categoriesList) {
                System.out.println(categories.getId() + " - "
                        + categories.getLeftKey() + "-" + categories.getRightKey() + " - " + categories.getName());
            }

            System.out.println("Выберете категорию для удаления: ");
            String categoryToDelete = scanner.nextLine();
            Categories categories1 = manager.find(Categories.class, Long.parseLong(categoryToDelete));
            System.out.println(categories1.getId() + " - "
                    + categories1.getLeftKey() + "-" + categories1.getRightKey() + " - " + categories1.getName());
            int delRightKey = categories1.getRightKey();
            int newRightKey = categories1.getRightKey() - categories1.getLeftKey() + 1;
            int newLeftKey = categories1.getLeftKey();
            manager.getTransaction().begin();

            Query deleteKeys = manager.createQuery("delete from Categories where LeftKey >= ?1 and RightKey <= ?2");
            deleteKeys.setParameter(1, newLeftKey);
            deleteKeys.setParameter(2, delRightKey);
            deleteKeys.executeUpdate();

            Query updateRightKey = manager.createQuery("update Categories set RightKey = RightKey - ?2 where RightKey >= ?1");
            updateRightKey.setParameter(1, delRightKey);
            updateRightKey.setParameter(2, newRightKey);
            updateRightKey.executeUpdate();

            Query updateLeftKey = manager.createQuery("update Categories set LeftKey = LeftKey - ?2 where LeftKey > ?1");
            updateLeftKey.setParameter(1, delRightKey);
            updateLeftKey.setParameter(2, newRightKey);
            updateLeftKey.executeUpdate();

            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}
