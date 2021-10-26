import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class tree {
    public static void main(String[] args) {
        //описать сущность для таблицы
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

        // Комплетующие
        // - Процессоры
        // - - Intel
        // - - AMD
        // - ОЗУ

        /*TypedQuery<Categories> categoriesTypedQuery = manager.createQuery
                ("select c from Categories c ", Categories.class);*/
        //List<Categories> categories = categoriesTypedQuery.getResultList();
        //String x = "-";
       /* for (Categories category : categories) {
            String repeated = x.repeat(category.getLevelCategory());
            System.out.println(repeated + category.getName());
        }*/
        /*for (Categories category : categories) {
            String dashes = "";// обнуление цикла
            for (int i = 0; i < category.getLevelCategory(); i++) {
                dashes += x; // замена пустоты на -
            }
            System.out.println(dashes + category.getName());
    }*/
// запросить ID категрии в нутри которой необходимо создать новую категорию
// Название новой категории через NextLine
// правый ключ увеличить на два если он больше или равен правому ключу род.элемента (6)
// левый ключ увеличить на 2 если он больше или равен правому ключу родительского элемента

        // запросить id для освобождения места
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
}