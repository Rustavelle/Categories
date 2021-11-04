import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class H {
    public static void main(String[] args) {
        // создание [1]
        // Перемещение [2]
        // Удаление [3]
        //Выберите действие: _

        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

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
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();
        try {
            TypedQuery<Categories> categoriesTypedQuery = manager.createQuery
                    ("select c from Categories c", Categories.class);
            List<Categories> categoriesList = categoriesTypedQuery.getResultList();
            for (Categories categories : categoriesList) {
                System.out.println(categories.getId() + " - "
                        + categories.getLeftKey() + "-" + categories.getRightKey() + " - " + categories.getName());
            }
            Scanner scanner = new Scanner(System.in);
            System.out.println("Выберете id для добовления категории, либо 0 для создания новой категории: ");
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
                manager.getTransaction().commit();

            } else if (Long.parseLong(categoryName) != 0) {
                Categories categories1 = manager.find(Categories.class, Long.parseLong(categoryName));
                Query query = manager.createQuery("update Categories set RightKey = RightKey + 2 where RightKey >= ?1");
                query.setParameter(1, categories1.getRightKey());
                query.executeUpdate();
                Query query1 = manager.createQuery("update Categories set LeftKey = LeftKey + 2 where LeftKey > ?1");
                query1.setParameter(1, categories1.getRightKey());
                query1.executeUpdate();
                Categories category = new Categories();
                category.setLeftKey(categories1.getRightKey());
                category.setRightKey(categories1.getRightKey() + 1);
                category.setLevelCategory(categories1.getLevelCategory() + 1);
                System.out.println("Введите название новой категории: ");
                String NewCategory = scanner.nextLine();
                category.setName(NewCategory);
                manager.persist(category);
                manager.getTransaction().commit();
            }
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    private static void moving(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
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
            Scanner scanner = new Scanner(System.in);
            System.out.println("Выберете категорию что перенести : ");
            String category = scanner.nextLine();
            Categories categories1 = manager.find(Categories.class, Long.parseLong(category));
            System.out.println(categories1.getId() + " - "
                    + categories1.getLeftKey() + "-" + categories1.getRightKey() + " - " + categories1.getName());

            System.out.println("Выберете категорию куда перенести :");
            String categorySetId = scanner.nextLine();
            if (Long.parseLong(categorySetId) == 0) {
                Query query = manager.createQuery("update Categories set " +
                        "LeftKey = LeftKey * -1, RightKey = RightKey * -1 where LeftKey >= ?1 and RightKey <= ?2");
                query.setParameter(1, categories1.getLeftKey());
                query.setParameter(2, categories1.getRightKey());
                query.executeUpdate();

                int moveSize = categories1.getRightKey() - categories1.getLeftKey() + 1;
                Query query1 = manager.createQuery
                        ("update Categories set RightKey = RightKey - ?1 where RightKey >= ?2");
                query1.setParameter(1, moveSize);
                query1.setParameter(2, categories1.getRightKey());
                query1.executeUpdate();

                Query query2 = manager.createQuery("update Categories set LeftKey = LeftKey - ?1 where LeftKey > ?2");
                query2.setParameter(1, moveSize);
                query2.setParameter(2, categories1.getRightKey());
                query2.executeUpdate();

                TypedQuery<Integer> maxRightKeyQuery = manager.createQuery
                        ("select max(c.RightKey) from Categories c", Integer.class);
                List<Integer> maxRightKeyQueryList = maxRightKeyQuery.getResultList();
                int maxRightKey = maxRightKeyQueryList.get(0);
                Query query3 = manager.createQuery("update Categories set LeftKey = 0 - LeftKey - ?1 + ?2 + 1," +
                        "RightKey = 0 - RightKey - ?1 + ?2 + 1," +
                        " LevelCategory = LevelCategory - ?3  where LeftKey < 0 and RightKey < 0");
                query3.setParameter(1, categories1.getLeftKey());
                query3.setParameter(2, maxRightKey);
                query3.setParameter(3, categories1.getLevelCategory());
                query3.executeUpdate();

                manager.getTransaction().commit();
            } else if (Long.parseLong(categorySetId) != 0) {
                Query query = manager.createQuery("update Categories set " +
                        "LeftKey = LeftKey * -1, RightKey = RightKey * -1 where LeftKey >= ?1 and RightKey <= ?2");
                query.setParameter(1, categories1.getLeftKey());
                query.setParameter(2, categories1.getRightKey());
                query.executeUpdate();

                int moveSize = categories1.getRightKey() - categories1.getLeftKey() + 1;
                Query query1 = manager.createQuery
                        ("update Categories set RightKey = RightKey - ?1 where RightKey >= ?2");
                query1.setParameter(1, moveSize);
                query1.setParameter(2, categories1.getRightKey());
                query1.executeUpdate();

                Query query2 = manager.createQuery("update Categories set LeftKey = LeftKey - ?1 where LeftKey > ?2");
                query2.setParameter(1, moveSize);
                query2.setParameter(2, categories1.getRightKey());
                query2.executeUpdate();

                Categories NewParent = manager.find(Categories.class, Long.parseLong(categorySetId));
                manager.refresh(NewParent);
                System.out.println(NewParent.getId() + " - "
                        + NewParent.getLeftKey() + "-" + NewParent.getRightKey() + " - " + NewParent.getName());
                Query query3 = manager.createQuery("update Categories set RightKey = RightKey + ?1 where RightKey >= ?2");
                query3.setParameter(1, moveSize);
                query3.setParameter(2, NewParent.getRightKey());
                query3.executeUpdate();

                Query query4 = manager.createQuery("update Categories set LeftKey = LeftKey + ?1 where LeftKey > ?2");
                query4.setParameter(1, moveSize);
                query4.setParameter(2, NewParent.getRightKey());
                query4.executeUpdate();

                manager.refresh(NewParent);
                int sum1 = NewParent.getRightKey() - categories1.getRightKey() - 1;
                int sum3 = NewParent.getLevelCategory() - categories1.getLevelCategory() + 1;
                Query query5 = manager.createQuery
                        ("update Categories set LeftKey = 0 - LeftKey + ?1, RightKey = 0 - RightKey + ?1, LevelCategory = LevelCategory + ?2  " +
                                "where LeftKey < 0 and RightKey < 0");
                query5.setParameter(1, sum1);
                query5.setParameter(2, sum3);
                query5.executeUpdate();
                manager.getTransaction().commit();
            }
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }

    private static void delete(){
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        try {
            TypedQuery<Categories> categoriesTypedQuery = manager.createQuery
                    ("select c from Categories c", Categories.class);
            List<Categories> categoriesList = categoriesTypedQuery.getResultList();
            for (Categories categories : categoriesList) {
                System.out.println(categories.getId() + " - "
                        + categories.getLeftKey() + "-" + categories.getRightKey() + " - " + categories.getName());
            }
            Scanner scanner = new Scanner(System.in);
            System.out.println("Выберете категорию для удаления: ");
            String categoryToDelete = scanner.nextLine();
            Categories categories1 = manager.find(Categories.class, Long.parseLong(categoryToDelete));
            System.out.println(categories1.getId() + " - "
                    + categories1.getLeftKey() + "-" + categories1.getRightKey() + " - " + categories1.getName());
            int sum1 = categories1.getRightKey();
            int sum2 = categories1.getRightKey() - categories1.getLeftKey() + 1;
            int  sum3 = categories1.getLeftKey();
            manager.getTransaction().begin();
            Query query = manager.createQuery("delete from Categories where LeftKey >= ?1 and RightKey <= ?2");
            query.setParameter(1, sum3);
            query.setParameter(2, sum1);
            query.executeUpdate();
            Query query1 = manager.createQuery("update Categories set RightKey = RightKey - ?2 where RightKey >= ?1");
            query1.setParameter(1,sum1);
            query1.setParameter(2, sum2);
            Query query2 = manager.createQuery("update Categories set LeftKey = LeftKey - ?2 where LeftKey > ?1");
            query2.setParameter(1, sum1);
            query2.setParameter(2, sum2);
            query1.executeUpdate();
            query2.executeUpdate();
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}
