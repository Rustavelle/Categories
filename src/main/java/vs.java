public class vs {
    public static void main(String[] args) {
        String x = "000";
        System.out.println(x.matches("\\d*"));
        System.out.println(x);
    }
}
// class - шаблон с определенным объемом свойств и действий;
// объект - это отдельно взятая реализация класса  с четко заданными значениями свойств;
// объект - четко заданное имя и возраст (пример Человек - возраст , ФИО и т.д.);
// extends - можно вызывать методы родительского класса для дочернего но не наоборот; - принцип наследледования
// protected - в классе и дочерних классах
// private -  в классе
// public - везде во всех классах и папках
// инкапсуляция - принцип который говорит о том что имеет изолированное окружение.
// .toUpperCase - перевод в верхний регистр.
// Объетные типы данных - все которые не примитивные.
// Приметивные типы всегда передаются по значению, т.е. при записи примет.типа в переменную либо при передече через
//      параметр создается копия.
// При записи объектов в переменную либо при передаче параметров, используется не сам объект а ссылка на него в памяти.
// (Распаковка и упаковка типов) Объектные вариации приметивных типов, копируют свое значение так же как и премитивные типы,
// т.е int и Integer одинаковые в своем плане, даже если Integer является объектным типом.
