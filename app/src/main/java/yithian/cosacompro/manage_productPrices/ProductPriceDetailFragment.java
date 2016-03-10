package yithian.cosacompro.manage_productprices;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import yithian.cosacompro.R;
import yithian.cosacompro.db.DBPopulator;
import yithian.cosacompro.db.dbclasses.Product;
import yithian.cosacompro.db.dbclasses.ProductPrice;
import yithian.cosacompro.db.dbclasses.Seller;

public class ProductPriceDetailFragment extends Fragment {
    private ProductPrice current_productprice;
    private View rootView;
    private DBPopulator dbPopulator;
    private ArrayList<Product> product_arrayList;
    private ArrayList<String> prodNames_arrayList;
    private ArrayList<Seller> seller_arrayList;
    private ArrayList<String> selNames_arrayList;
    private int open_mode_flag;
    // UI stuff
    private Spinner prod_name_input;
    private Spinner seller_input;
    private TextView normal_price_input;
    private TextView special_price_input;
    private TextView special_data_input;
    private Button applyProductPriceChanges_button;

    public ProductPriceDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey("open_mode_flag")) {
            // Load values passed by ProductPriceDetailActivity
            open_mode_flag = getArguments().getInt("open_mode_flag");
            if (open_mode_flag != 2) {
                int productprice_id = getArguments().getInt("cur_productprice_id");
                int product_id = getArguments().getInt("cur_productprice_prod");
                int seller_id = getArguments().getInt("cur_productprice_seller");
                double normal_price = getArguments().getDouble("cur_productprice_norm");
                double special_price = getArguments().getDouble("cur_productprice_spec");
                String special_data = getArguments().getString("cur_productprice_specdate");
                current_productprice = new ProductPrice(productprice_id, product_id, seller_id, normal_price, special_price, special_data);
            }

            // Spinner(s) stuff
            dbPopulator = new DBPopulator(this.getContext(), null, null, 1);
            product_arrayList = dbPopulator.getProductHandler().getProducts();
            seller_arrayList = dbPopulator.getSellerHandler().getSellers();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.manage_productprices_detail, container, false);
        // Initialize the UI
        prod_name_input = (Spinner) rootView.findViewById(R.id.prod_name_input);
        seller_input = (Spinner) rootView.findViewById(R.id.seller_input);
        normal_price_input = (TextView) rootView.findViewById(R.id.normal_price_input);
        special_price_input = (TextView) rootView.findViewById(R.id.special_price_input);
        special_data_input = (TextView) rootView.findViewById(R.id.special_data_input);
        applyProductPriceChanges_button = (Button) rootView.findViewById(R.id.applyProductPriceChanges_button);

        // Configure the prod_name_input Spinner
        prodNames_arrayList = new ArrayList<>();
        for (int i = 0; i < product_arrayList.size(); i++) {
            prodNames_arrayList.add(product_arrayList.get(i).getProduct_name());
        }
        ArrayAdapter<String> prod_name_inputadapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, prodNames_arrayList);
        prod_name_inputadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        prod_name_input.setAdapter(prod_name_inputadapter);
        prod_name_input.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextSize(14);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Configure the seller_input Spinner
        selNames_arrayList = new ArrayList<>();
        for (int i = 0; i < seller_arrayList.size(); i++) {
            selNames_arrayList.add(seller_arrayList.get(i).getSeller_name());
        }
        ArrayAdapter<String> seller_inputadapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, selNames_arrayList);
        seller_inputadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        seller_input.setAdapter(seller_inputadapter);
        seller_input.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextSize(14);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        switch (open_mode_flag) {
            case 0:
                openReadOnly();
                break;
            case 1:
                openEditMode();
                break;
            case 2:
                openAddMode();
                break;
            default:
                openReadOnly();
                break;
        }
        return rootView;
    }

    private void loadProductPriceValues() {
        String current_productName = dbPopulator.getProductHandler().getProductNameByID(current_productprice.getProduct_id());
        String current_sellerName = dbPopulator.getSellerHandler().getSellerNameByID(current_productprice.getSeller_id());
        if ((current_productName != null) && (current_sellerName != null)) {
            prod_name_input.setSelection(prodNames_arrayList.indexOf(current_productName));
            seller_input.setSelection(selNames_arrayList.indexOf(current_sellerName));
            normal_price_input.setText(String.valueOf(current_productprice.getNormal_price()));
            special_price_input.setText(String.valueOf(current_productprice.getSpecial_price()));
            special_data_input.setText(current_productprice.getSpecial_date());
        }
    }

    private void triggerUI(boolean trigger) {
        // Trigger input fields
        prod_name_input.setEnabled(trigger);
        seller_input.setEnabled(trigger);
        normal_price_input.setEnabled(trigger);
        special_price_input.setEnabled(trigger);
        special_data_input.setEnabled(trigger);
        // Set focus
        prod_name_input.setFocusable(trigger);
        seller_input.setFocusable(trigger);
        normal_price_input.setFocusable(trigger);
        special_price_input.setFocusable(trigger);
        special_data_input.setFocusable(trigger);
        // Trigger the input button
        if (trigger)
            applyProductPriceChanges_button.setVisibility(View.VISIBLE);
        else applyProductPriceChanges_button.setVisibility(View.GONE);
    }

    private void openReadOnly() {
        if (current_productprice != null) {
            // Load values in UI
            loadProductPriceValues();
            // Set title on top
            this.getActivity().setTitle(R.string.title_product_info);
            // Disable UI
            triggerUI(false);
        }
    }

    private void openEditMode() {
        if (current_productprice != null) {
            // Load values in UI
            loadProductPriceValues();
            // Set title on top
            this.getActivity().setTitle(R.string.title_product_edit);
            // Enable UI
            triggerUI(true);

            applyProductPriceChanges_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String temp;
                    // Update current Product
                    current_productprice.setProduct_id(product_arrayList.get(prod_name_input.getSelectedItemPosition()).getProduct_id());
                    current_productprice.setSeller_id(seller_arrayList.get(seller_input.getSelectedItemPosition()).getSeller_id());

                    temp = normal_price_input.getText().toString();
                    if (!temp.equals(""))
                        current_productprice.setNormal_price(Double.valueOf(temp));

                    temp = special_price_input.getText().toString();
                    if (!temp.equals(""))
                        current_productprice.setSpecial_price(Double.valueOf(temp));
                    current_productprice.setSpecial_date(special_data_input.getText().toString());
                    // Update the DB
                    boolean addResult = dbPopulator.getProductPriceHandler().updateProductPrice(current_productprice);
                    if (!addResult) {
                        Snackbar.make(getView(), "ooops! Hai provato ad inserire una prezzatura già esistente!", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        // Go back to the previous screen
                        NavUtils.navigateUpTo(getActivity(), new Intent(v.getContext(), ProductPriceListActivity.class));
                    }
                }
            });
        }
    }

    private void openAddMode() {
        // Set title on top
        this.getActivity().setTitle(R.string.title_product_add);
        // Enable UI
        triggerUI(true);

        applyProductPriceChanges_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp;
                // Update current Product
                int product_id = product_arrayList.get(prod_name_input.getSelectedItemPosition()).getProduct_id();
                int seller_id = seller_arrayList.get(seller_input.getSelectedItemPosition()).getSeller_id();

                double norm_price = 0;
                temp = normal_price_input.getText().toString();
                if (!temp.equals(""))
                    norm_price = Double.valueOf(temp);

                double spec_price = 0;
                temp = special_price_input.getText().toString();
                if (!temp.equals(""))
                    spec_price = Double.valueOf(temp);
                String spec_date = special_data_input.getText().toString();
                current_productprice = new ProductPrice(product_id, seller_id, norm_price, spec_price, spec_date);
                // Update the DB
                boolean addResult = dbPopulator.getProductPriceHandler().addProductPrice(current_productprice);
                if (!addResult) {
                    Snackbar.make(getView(), "ooops! Hai provato ad inserire una prezzatura già esistente!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    // Go back to the previous screen
                    NavUtils.navigateUpTo(getActivity(), new Intent(v.getContext(), ProductPriceListActivity.class));
                }
            }
        });
    }
}
