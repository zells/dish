package org.zells.cortex.synapses.keyvalue;

import org.zells.cortex.Synapse;
import org.zells.dish.Dish;
import org.zells.dish.delivery.Address;
import org.zells.dish.delivery.Message;
import org.zells.dish.delivery.messages.AddressMessage;
import org.zells.dish.delivery.messages.NullMessage;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.List;

public class KeyValueEditorSynapse extends Synapse {

    private final KeyValueEditor model;

    private Map<String, Message> entries = new HashMap<String, Message>();
    private List<String> keys;

    public KeyValueEditorSynapse(String nameOrAddress, Address target, Dish dish) {
        super("KeyValueEditor: " + nameOrAddress, target);

        final TableModel tableModel = new TableModel();

        model = new KeyValueEditor(target, dish) {
            @Override
            protected void onUpdate(Map<String, Message> updatedEntries) {
                entries = updatedEntries;
                keys = new ArrayList<String>(entries.keySet());
                Collections.sort(keys);

                tableModel.fireTableDataChanged();
            }
        };
        setModel(model);

        setLayout(new BorderLayout());
        add(new JScrollPane(new JTable(tableModel)));
        add(createAddButton(), BorderLayout.SOUTH);
    }

    private Component createAddButton() {
        JButton button = new JButton("Add");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(
                        KeyValueEditorSynapse.this,
                        "Key",
                        "New entry",
                        JOptionPane.PLAIN_MESSAGE);

                if (name != null && !name.trim().isEmpty()) {
                    model.put(name, new NullMessage());
                }
            }
        });
        return button;
    }

    private class TableModel extends AbstractTableModel {

        public String getColumnName(int col) {
            switch (col) {
                case 0:
                    return "key";
                case 1:
                    return "value";
                default:
                    return null;
            }
        }

        public int getRowCount() {
            return entries.size();
        }

        public int getColumnCount() {
            return 2;
        }

        public Object getValueAt(int row, int col) {
            if (col == 0) {
                return keys.get(row);
            } else {
                return entries.get(keys.get(row));
            }
        }

        public boolean isCellEditable(int row, int col) {
            return true;
        }

        public void setValueAt(Object value, int row, int col) {
            if (value instanceof String) {
                String string = (String) value;
                if (col == 1) {
                    model.put(keys.get(row), new AddressMessage(Address.fromString(string)));
                    fireTableCellUpdated(row, col);
                } else if (!keys.get(row).equals(string)) {
                    model.remove(keys.get(row));
                    if (!string.trim().isEmpty()) {
                        model.put(string, entries.get(keys.get(row)));
                    }
                }
            }
        }
    }
}
