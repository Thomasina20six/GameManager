package blackjack.model;


public abstract class Participant {
    private String name;
    private Hand hand;
    private int money;
    private int currentBet;
    private boolean stood;

    public Participant(String name, int startingMoney) {
        this.name = name;
        this.money = startingMoney;
        this.hand = new Hand();
        this.stood = false;
    }

    public String getName() {
        return name;
    }

    public Hand getHand() {
        return hand;
    }

    public int getMoney() {
        return money;
    }

    public int getCurrentBet() {
        return currentBet;
    }

    public void addCard(Card card) {
        hand.addCard(card);
    }

    public void clearHand() {
        hand.clear();
        stood = false;
    }

    public void stand() {
        stood = true;
    }

    public boolean isBust() {
        return hand.isBust();
    }

    public int getHandValue() {
        return hand.getBestValue();
    }

    public boolean canBet(int amount) {
        return amount > 0 && amount <= money;
    }

    public boolean placeBet(int amount) {
        if(amount > 0 && amount <= money) {
            currentBet = amount;
            money -= amount;
            return true;
        }
        else{
            return false;
        }
    }

    public void winBet() {
        money += currentBet * 2;
        currentBet = 0;
    }

    public void loseBet() {
        currentBet = 0;
    }

    public void tieBet(){
        money += currentBet;
        currentBet = 0;
    }

    public void restoreFromSave(int money, int currentBet, java.util.List<Card> cards){
        this.money = money;
        this.currentBet = currentBet;
        this.hand.clear();
        this.stood = false;
        for(Card card : cards){
            this.hand.addCard(card);
        }
    }

    /** Reset wallet and clear table stakes (e.g. "Start Over" with a fresh bankroll). */
    public void resetBankroll(int amount) {
        this.money = amount;
        this.currentBet = 0;
        this.stood = false;
    }

}