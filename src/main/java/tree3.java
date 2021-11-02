import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class tree3 {
    public static void main(String[] args) {
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
                // перенос в отрицательные числа
                query.setParameter(1, categories1.getLeftKey());
                query.setParameter(2, categories1.getRightKey());
                query.executeUpdate();

                int moveSize = categories1.getRightKey() - categories1.getLeftKey() + 1;
                Query query1 = manager.createQuery
                        ("update Categories set RightKey = RightKey - ?1 where RightKey >= ?2");
                //освобождение места
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

                // ЛевыйКлючь = 0 - левый ключ + махПравыйКлюч + 1
                manager.getTransaction().commit();
            } else if (Long.parseLong(categorySetId) != 0) {
                Query query = manager.createQuery("update Categories set " +
                        "LeftKey = LeftKey * -1, RightKey = RightKey * -1 where LeftKey >= ?1 and RightKey <= ?2");
                // перенос в отрицательные числа
                query.setParameter(1, categories1.getLeftKey());
                query.setParameter(2, categories1.getRightKey());
                query.executeUpdate();

                int moveSize = categories1.getRightKey() - categories1.getLeftKey() + 1;
                Query query1 = manager.createQuery
                        ("update Categories set RightKey = RightKey - ?1 where RightKey >= ?2");
                //освобождение места
                query1.setParameter(1, moveSize);
                query1.setParameter(2, categories1.getRightKey());
                query1.executeUpdate();

                Query query2 = manager.createQuery("update Categories set LeftKey = LeftKey - ?1 where LeftKey > ?2");
                query2.setParameter(1, moveSize);
                query2.setParameter(2, categories1.getRightKey());
                query2.executeUpdate();

                Categories NewParent = manager.find(Categories.class, Long.parseLong(categorySetId));
                manager.refresh(NewParent);//Обновляет данные сущности в соответсвии с БД.
                System.out.println(NewParent.getId() + " - "
                        + NewParent.getLeftKey() + "-" + NewParent.getRightKey() + " - " + NewParent.getName());

                Query query3 = manager.createQuery("update Categories set RightKey = RightKey + ?1 where RightKey >= ?2");
                // выделение места для переноса
                query3.setParameter(1, moveSize);
                query3.setParameter(2, NewParent.getRightKey());
                query3.executeUpdate();

                Query query4 = manager.createQuery("update Categories set LeftKey = LeftKey + ?1 where LeftKey > ?2");
                query4.setParameter(1, moveSize);
                query4.setParameter(2, NewParent.getRightKey());
                query4.executeUpdate();
                // отрицательное в положительное

                manager.refresh(NewParent);
                int sum1 = NewParent.getRightKey() - categories1.getRightKey() - 1;
                int sum3 = NewParent.getLevelCategory() - categories1.getLevelCategory() + 1;
                Query query5 = manager.createQuery
                        ("update Categories set LeftKey = 0 - LeftKey + ?1, RightKey = 0 - RightKey + ?1, LevelCategory = LevelCategory + ?2  " +
                                "where LeftKey < 0 and RightKey < 0");
                query5.setParameter(1, sum1);
                query5.setParameter(2, sum3);
                query5.executeUpdate();
                // level = level + (уровень нового родителя - уровень перемещаемого элемента + 1)
                // left = 0 - left + (правый ключ нового родителя - правый ключ освовного перемещаемого элемента - 1)
                // right = 0 - right + (правый ключ нового родителя - правый ключ освовного перемещаемого элемента - 1)
                manager.getTransaction().commit();
            }
        } catch (Exception e) {
            manager.getTransaction().rollback();
            e.printStackTrace();
        }
    }
}
