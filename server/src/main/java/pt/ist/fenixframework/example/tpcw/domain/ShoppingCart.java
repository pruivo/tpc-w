package pt.ist.fenixframework.example.tpcw.domain;

import java.sql.Timestamp;

import pt.ist.fenixframework.example.tpcw.domain.dto.CartDTO;

public class ShoppingCart extends ShoppingCart_Base {
    
    private ShoppingCart() {}

    public  ShoppingCart(int sc_id) {
        super();

	setSc_id(sc_id);
	resetTime();
    }

    // get shopping cart line for item id
    public ShoppingCartLine getShoppingCartLine(int i_id) {
	for (ShoppingCartLine scl : getShoppingCartLinesSet()) {
	    if (scl.getBook().getI_id() == i_id) {
		return scl;
	    }
	}
	return null;
    }
    
    public void addItem(Book book) {
	int i_id = book.getI_id();
	ShoppingCartLine scl = getShoppingCartLine(i_id);
	if (scl == null) {
            ShoppingCartLine newLine = new ShoppingCartLine(book, 1);
	    this.addShoppingCartLines(newLine);
	} else {
	    scl.setQty(scl.getQty()+1);
	}
    }

    public void addRandomItemToCartIfNecessary() {
	if (getShoppingCartLinesSet().isEmpty()) {
	    // Cart is empty
	    Book relatedBook = getApp().getRandomBook().getRelatedTo1();
	    addItem(relatedBook);
	}
    }

    public void resetTime() {
	Timestamp ts = new Timestamp(System.currentTimeMillis());
	ts.setNanos(0);
	setTime(ts);
    }

    public CartDTO getDTO(double discount) {
	return new CartDTO(this, discount);
    }
}
