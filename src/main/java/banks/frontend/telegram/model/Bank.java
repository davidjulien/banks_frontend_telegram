package banks.frontend.telegram.model;

public class Bank {
  private String id;
  private String name;

  public Bank(String id, String name) {
    this.id = id;
    this.name = name;
  }

  public String getName() {
    return this.name;
  }

  public boolean equals(Object o) {
    if (this == o) {
      return true;
    } else if (o instanceof Bank) {
      Bank b = (Bank)o;
      return this.id.equals(b.id) && this.name.equals(b.name);
    } else {
      return false;
    }
  }
}
