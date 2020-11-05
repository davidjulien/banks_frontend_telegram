package banks.frontend.telegram.model;

public class Store {
  private int id;
  private String name;

  public Store(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}
