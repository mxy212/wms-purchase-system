package wms.purchase;

public class CancelledState extends AbstractPurchaseOrderState {
    @Override
    public String getName() {
        return "已取消";
    }
}
