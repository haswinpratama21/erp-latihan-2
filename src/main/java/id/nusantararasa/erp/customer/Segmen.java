package id.nusantararasa.erp.customer;

public enum Segmen {
    DISTRIBUTOR("Distributor"),
    RETAIL_MODERN("Retail Modern"),
    TOKO("Toko");

    private final String label;

    Segmen(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
