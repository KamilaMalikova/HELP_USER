package com.kamilamalikova.help.ui.stock.adapter;

import android.os.Parcel;
import android.os.Parcelable;

import com.kamilamalikova.help.model.Product;
import com.kamilamalikova.help.model.StockInventory;
import com.kamilamalikova.help.model.StockItemBalance;

public class ProductItemObject implements Parcelable {
    private boolean isChosen;
    private StockItemBalance product;
    private Double qty;

    public ProductItemObject(boolean isChosen, StockItemBalance product, Double qty) {
        this.isChosen = isChosen;
        this.product = product;
        this.qty = qty;
    }

    protected ProductItemObject(Parcel in) {
        isChosen = in.readByte() != 0;
        product = in.readParcelable(Product.class.getClassLoader());
        if (in.readByte() == 0) {
            qty = null;
        } else {
            qty = in.readDouble();
        }
    }

    public static final Creator<ProductItemObject> CREATOR = new Creator<ProductItemObject>() {
        @Override
        public ProductItemObject createFromParcel(Parcel in) {
            return new ProductItemObject(in);
        }

        @Override
        public ProductItemObject[] newArray(int size) {
            return new ProductItemObject[size];
        }
    };

    public boolean isChosen() {
        return isChosen;
    }

    public void setChosen(boolean chosen) {
        isChosen = chosen;
    }

    public StockItemBalance getProduct() {
        return product;
    }

    public void setProduct(StockItemBalance product) {
        this.product = product;
    }

    public Double getQty() {
        return qty;
    }

    public void setQty(Double qty) {
        this.qty = qty;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeBoolean(isChosen);
        dest.writeParcelable(product, flags);
        dest.writeDouble(qty);
    }
}
