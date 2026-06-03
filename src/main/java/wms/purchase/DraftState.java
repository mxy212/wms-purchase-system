package wms.purchase;

public class DraftState extends AbstractPurchaseOrderState {
    @Override
    public String getName() {
        return "草稿";
    }

    @Override
    public void edit(PurchaseOrder order, String product, int quantity, String supplier) {
        order.updateDetails(product, quantity, supplier);
        order.record("编辑采购单", getName(), getName());
    }

    @Override
    public void approve(PurchaseOrder order) {
        order.changeState(new ApprovedState(), "审批采购单");
    }

    @Override
    public void cancel(PurchaseOrder order) {
        order.changeState(new CancelledState(), "取消采购单");
    }
}
