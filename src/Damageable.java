/**
 * Represents a game entity that has health, can take damage and can die.
 * Implemented by {@link Player} and {@link EnemyBoss}.
 */
public interface Damageable {

    /**
     * Reduces the entity's health by the given amount.
     *
     * @param amount the amount of damage to apply
     */
    void takeDamage(double amount);

    /**
     * @return the entity's current health, as a fraction of full health (1.0 = full)
     */
    double getHealth();

    /**
     * @return true if the entity's health is above zero
     */
    boolean isAlive();
}
