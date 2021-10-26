import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class tree3 {
    //Перемещение категории
    //вводим айди элементы который хотим перенести
    //  вводим айди элемента куда хотим переместить
    // 6 update запросов
    // 1 - сделать левый и правый ключи перемещаемым элементом отрицательными т.е. ключи 2 7 3 4 5 6 -> -2 -7 -3 -4 -5 -6 l = l * -1 r = r * -1
    // 2 - убрать оброзовавшийся промежуток
    // 3 - выделить место для вствки
    // 4 - переместить элементы с отрицательными ключами в новое место и сдеалть коректными (т.е. положительными и по счету)
    // Добавить новый элемент родительской категории
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

// при наж 0 перенести в новую родюкатегорию
// перенос в отриц
// убрать промежуток
// с отриц в положительные и порядо

/* метод Игоря
System.out.print("Что перемещать: ");
        String moveIdIn = scanner.nextLine();
        long moveId = Long.parseLong(moveIdIn);
        Tree move = manager.find(Tree.class, moveId);
        int moveSize = move.getRight() - move.getLeft() + 1;

        System.out.print("Куда перемещать: ");
        String parentIdIn = scanner.nextLine();
        long parentId = Long.parseLong(parentIdIn);
        Tree parent = manager.find(Tree.class, parentId);

        Query toNegativeQuery = manager.createQuery(
        "update Tree t set t.left = -t.left, t.right = -t.right where t.left >= ?1 and t.right <= ?2"
        );
        toNegativeQuery.setParameter(1, move.getLeft());
        toNegativeQuery.setParameter(2, move.getRight());
        toNegativeQuery.executeUpdate();

        Query removeLeftSpaceQuery = manager.createQuery(
        "update Tree t set t.left = t.left - ?1 where t.left > ?2"
        );
        removeLeftSpaceQuery.setParameter(1, moveSize);
        removeLeftSpaceQuery.setParameter(2, move.getRight());
        removeLeftSpaceQuery.executeUpdate();

        Query removeRightSpaceQuery = manager.createQuery(
        "update Tree t set t.right = t.right - ?1 where t.right > ?2"
        );
        removeRightSpaceQuery.setParameter(1, moveSize);
        removeRightSpaceQuery.setParameter(2, move.getRight());
        removeRightSpaceQuery.executeUpdate();

        manager.refresh(parent);

        Query createLeftSpaceQuery = manager.createQuery(
        "update Tree t set t.left = t.left + ?1 where t.left >= ?2"
        );
        createLeftSpaceQuery.setParameter(1, moveSize);
        createLeftSpaceQuery.setParameter(2, parent.getRight());
        createLeftSpaceQuery.executeUpdate();

        Query createRightSpaceQuery = manager.createQuery(
        "update Tree t set t.right = t.right + ?1 where t.right >= ?2"
        );
        createRightSpaceQuery.setParameter(1, moveSize);
        createRightSpaceQuery.setParameter(2, parent.getRight());
        createRightSpaceQuery.executeUpdate();

        Query toPositiveQuery = manager.createQuery(
        "update Tree t set t.left = 0 - t.left + ?1, t.right = 0 - t.right + ?1, t.level = t.level + ?2 where t.left < 0"
        );
        toPositiveQuery.setParameter(1, parent.getRight() - move.getRight() - 1);
        toPositiveQuery.setParameter(2, parent.getLevel() - move.getLevel() + 1);
        toPositiveQuery.executeUpdate();*/