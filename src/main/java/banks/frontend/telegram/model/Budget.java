package banks.frontend.telegram.model;

public class Budget {
  private int id;
  private String name;

  public Budget(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getName() {
    return this.name;
  }
}
