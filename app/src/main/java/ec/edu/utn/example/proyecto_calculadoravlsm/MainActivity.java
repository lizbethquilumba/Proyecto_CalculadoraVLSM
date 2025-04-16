package ec.edu.utn.example.proyecto_calculadoravlsm;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private LinearLayout hostInputContainer;
    private List<EditText> hostInputFields;
    private Button calculateButton;
    private EditText ipBaseField;
    private Spinner cidrCombo;
    private EditText numSubredesEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        ipBaseField = findViewById(R.id.ip_base_entry);
        cidrCombo = findViewById(R.id.cidr_combo);
        numSubredesEditText = findViewById(R.id.num_subredes_spin);
        hostInputContainer = findViewById(R.id.host_frame);
        calculateButton = findViewById(R.id.calc_button);

        // Initialize the list for input fields
        hostInputFields = new ArrayList<>(); // Initialize the list to avoid NullPointerException

        // Set up CIDR Spinner
        ArrayAdapter<CharSequence> cidrAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.cidr_values, // CIDR values should be defined in strings.xml
                android.R.layout.simple_spinner_item
        );
        cidrAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cidrCombo.setAdapter(cidrAdapter);

        // Add text change listener for subnets input field
        numSubredesEditText.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int before, int count) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                updateHostFields(); // Update host fields when the number of subnets changes
            }

            @Override
            public void afterTextChanged(android.text.Editable editable) {}
        });

        // Set onClickListener for the calculate button
        calculateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCalculateClicked(v);
            }
        });
    }

    // This method updates the host input fields based on the number of subnets
    private void updateHostFields() {
        hostInputContainer.removeAllViews();

        String inputText = numSubredesEditText.getText().toString().trim();
        if (inputText.isEmpty()) {
            return; // No hacemos nada si está vacío
        }

        int numSubnets;
        try {
            numSubnets = Integer.parseInt(inputText);
        } catch (NumberFormatException e) {
            return; // Error de número inválido
        }

        hostInputFields.clear();

        for (int i = 0; i < numSubnets; i++) {
            TextView label = new TextView(this);
            label.setText("Hosts para Subred " + (i + 1) + ":");
            hostInputContainer.addView(label);

            EditText hostField = new EditText(this);
            hostField.setHint("Ingresa número de hosts");
            hostInputFields.add(hostField);
            hostInputContainer.addView(hostField);
        }
    }

    // This method is called when the "Calcular Subredes" button is clicked
    public void onCalculateClicked(View view) {
        // Get the host values from the input fields
        List<Integer> hosts = new ArrayList<>();
        for (EditText hostField : hostInputFields) {
            try {
                hosts.add(Integer.parseInt(hostField.getText().toString()));
            } catch (NumberFormatException e) {
                // Handle invalid host input
                hostField.setError("Por favor, ingresa un número válido.");
                return;
            }
        }

        // Get IP and CIDR values
        String ipBase = ipBaseField.getText().toString();
        String cidr = (String) cidrCombo.getSelectedItem();

        // Perform the VLSM calculation (use a separate class for calculation, if needed)
        VLSMCalculator calculator = new VLSMCalculator(ipBase, hosts, Integer.parseInt(cidr));
        try {
            List<VLSMCalculator.SubnetResult> results = calculator.calculate();
            showResultsDialog(results);  // Show results in a dialog with table
        } catch (Exception e) {
            showPopupMessage("Error: " + e.getMessage());
        }
    }

    // This method shows the calculated subnet results in a dialog with a table
    private void showResultsDialog(List<VLSMCalculator.SubnetResult> results) {
        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Resultados de Subredes VLSM");

        // Inflate the custom layout for the dialog
        View dialogView = LayoutInflater.from(this).inflate(R.layout.subnet_results_layout, null);
        builder.setView(dialogView);

        // Get the table layout from the view
        TableLayout tableLayout = dialogView.findViewById(R.id.results_table);

        // Populate table with data
        boolean alternateRowColor = false;
        for (VLSMCalculator.SubnetResult subnet : results) {
            TableRow row = new TableRow(this);

            // Set alternating row colors
            if (alternateRowColor) {
                row.setBackgroundColor(Color.parseColor("#F2F2F2"));
            } else {
                row.setBackgroundColor(Color.WHITE);
            }
            alternateRowColor = !alternateRowColor;

            // Add subnet ID cell
            addCell(row, "Subred " + subnet.getSubredId());

            // Add IP Base cell
            addCell(row, subnet.getIpBase());

            // Add CIDR cell
            addCell(row, "/" + subnet.getCidr());

            // Add Hosts Requested cell
            addCell(row, String.valueOf(subnet.getHostsRequested()));

            // Add Hosts Available cell
            addCell(row, String.valueOf(subnet.getHostsAvailable()));

            // Add IP Range cell
            addCell(row, subnet.getRangeIps());

            // Add Broadcast cell
            addCell(row, subnet.getBroadcast());

            // Add the row to the table
            tableLayout.addView(row);
        }

        // Add buttons to close the dialog
        builder.setPositiveButton("Cerrar", null);

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    // Helper method to add a cell to a table row
    private void addCell(TableRow row, String text) {
        TextView cell = new TextView(this);
        cell.setText(text);
        cell.setPadding(10, 10, 10, 10);
        cell.setTextColor(Color.BLACK);
        row.addView(cell);
    }

    // Utility method to show popup messages
    private void showPopupMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}