import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class tree2 {
    public static void main(String[] args) {
        EntityManagerFactory factory = Persistence.createEntityManagerFactory("main");
        EntityManager manager = factory.createEntityManager();

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
        try {
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
