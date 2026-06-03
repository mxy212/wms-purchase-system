package wms.purchase;

public final class BusinessDocumentFactory {
    private BusinessDocumentFactory() {
    }

    public static String createInboundOrderNo(String purchaseOrderNo) {
        if (purchaseOrderNo == null || purchaseOrderNo.trim().isEmpty()) {
            throw new PurchaseOrderException("采购单号不能为空");
        }
        return "IN-" + purchaseOrderNo;
    }

    public static String createInvoiceNo(String purchaseOrderNo) {
        if (purchaseOrderNo == null || purchaseOrderNo.trim().isEmpty()) {
            throw new PurchaseOrderException("采购单号不能为空");
        }
        return "INV-" + purchaseOrderNo;
    }

    public static String createReturnOrderNo(String purchaseOrderNo) {
        if (purchaseOrderNo == null || purchaseOrderNo.trim().isEmpty()) {
            throw new PurchaseOrderException("采购单号不能为空");
        }
        return "RT-" + purchaseOrderNo;
    }
}
