package blackjack.model;

import java.util.ArrayList;
import java.util.List;

public class Hand {
    private List<Card> cards;
    private boolean soft17 = false;

    public Hand() {
        cards = new ArrayList<>();
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void clear() {
        cards.clear();
    }

    public List<Card> getCards() {
        return new ArrayList<>(cards);
    }

    public int getBestValue() {
        int total = 0;
        int aceCount = 0;

        for (Card card : cards) {
            total += card.getValue();

            if (card.getRank() == Rank.ACE) {
                aceCount++;
            }
        }

        while (total > 21 && aceCount > 0) {
            total -= 10;
            aceCount--;
        }
        if(total == 17 && aceCount == 1) {
            soft17 = true;
        }

        return total;
    }
    public boolean isSoft17() {
        
        soft17 = false;
        if(getBestValue()!=17){
            return false;
        }
        else{
            return soft17;
        }
    }


    public boolean isBust() {
        return getBestValue() > 21;
    }


}