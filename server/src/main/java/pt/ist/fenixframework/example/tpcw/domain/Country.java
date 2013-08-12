package pt.ist.fenixframework.example.tpcw.domain;

public class Country extends Country_Base {
    
    private Country() {}

    public Country(String name, double currency, String exchange, int co_id) {
	super();
    
	setName(name);
	setCurrency(currency);
	setExchange(exchange);
	setCo_id(co_id);
    }

    public Address findOrCreateAddress(String street1, String street2, String city, String state, String zip) {
	Address foundAddress = null;
	for (Address address : getAddressesSet()) {
	    if (address.matchesExceptCountry(street1, street2, city, state, zip)) {
		return address;
	    }
	}
        App app = getApp();
	int addr_id = app.getNumAddrIds() + 1;
	app.setNumAddrIds(addr_id);
	Address address = new Address(street1, street2, city, state, zip, this, addr_id);
        return address;
    }
}
