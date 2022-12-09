package code;

public class Ship {
    int numberOfPeopleOntheShip;
    int damage;
    boolean wreck;
    boolean blackboxtaken;
    int positionXOftheShip;
    int positionYOftheShip;
    boolean retrievable;

    public Ship() {
        this.numberOfPeopleOntheShip = 0;
        damage = 1;
        wreck = false;
        this.positionXOftheShip = 0;
        this.positionYOftheShip = 0;
        blackboxtaken = false;
        retrievable = true;
    }

    // tostring
    public String toString() {
        return "{numberOfPeopleOntheShip=" + numberOfPeopleOntheShip + ", damage=" + damage + ", wreck=" + wreck
                + ", blackboxtaken=" + blackboxtaken + ", positionXOftheShip=" + positionXOftheShip
                + ", positionYOftheShip=" + positionYOftheShip + ", retrievable=" + retrievable + "}";
    }
}
