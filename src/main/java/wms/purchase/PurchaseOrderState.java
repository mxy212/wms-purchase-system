package wms.purchase;

public interface PurchaseOrderState {
    String getName();

    void edit(PurchaseOrder order, String product, int quantity, String supplier);

    void approve(PurchaseOrder order);

    void cancel(PurchaseOrder order);

    void generateInboundOrder(PurchaseOrder order);

    void generateInvoice(PurchaseOrder order);

    void pay(PurchaseOrder order);

    void returnAndRefund(PurchaseOrder order);
}
