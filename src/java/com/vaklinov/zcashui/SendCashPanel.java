/************************************************************************************************
 *  _________          _     ____          _           __        __    _ _      _   _   _ ___
 * |__  / ___|__ _ ___| |__ / ___|_      _(_)_ __   __ \ \      / /_ _| | | ___| |_| | | |_ _|
 *   / / |   / _` / __| '_ \\___ \ \ /\ / / | '_ \ / _` \ \ /\ / / _` | | |/ _ \ __| | | || |
 *  / /| |__| (_| \__ \ | | |___) \ V  V /| | | | | (_| |\ V  V / (_| | | |  __/ |_| |_| || |
 * /____\____\__,_|___/_| |_|____/ \_/\_/ |_|_| |_|\__, | \_/\_/ \__,_|_|_|\___|\__|\___/|___|
 *                                                 |___/
 *
 * Copyright (c) 2016 Ivan Vaklinov <ivan@vaklinov.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 **********************************************************************************/
package com.vaklinov.zcashui;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;

import com.vaklinov.zcashui.ZCashClientCaller.WalletCallException;


/**
 * ... for sending cash
 *
 * @author Ivan Vaklinov <ivan@vaklinov.com>
 */
public class SendCashPanel
	extends JPanel
{
	private ZCashClientCaller clientCaller;
	private StatusUpdateErrorReporter errorReporter;
	
	private JComboBox  balanceAddressCombo     = null;
	private JPanel     comboBoxParentPanel     = null;
	private String[][] lastAddressBalanceData  = null;
	private String[]   comboBoxItems           = null;
	
	private JTextField destinationAddressField = null;
	private JTextField destinationAmountField  = null;
	private JTextField destinationMemoField    = null;	
	private JButton    sendButton              = null;
	
	private JPanel       operationStatusPanel        = null;
	private JLabel       operationStatusLabel        = null;
	private JProgressBar operationStatusProhgressBar = null;
	private Timer        operationStatusTimer        = null;
	private String       operationStatusID           = null;
	private int          operationStatusCounter      = 0;

	public SendCashPanel(ZCashClientCaller clientCaller,  StatusUpdateErrorReporter errorReporter)
		throws IOException, InterruptedException, WalletCallException
	{
		this.clientCaller = clientCaller;
		this.errorReporter = errorReporter;

		// Build content
		this.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		this.setLayout(new BorderLayout());
		JPanel sendCashPanel = new JPanel();
		this.add(sendCashPanel, BorderLayout.NORTH);
		sendCashPanel.setLayout(new BoxLayout(sendCashPanel, BoxLayout.Y_AXIS));
		sendCashPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		JPanel tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tempPanel.add(new JLabel("Send cash from:"));
		sendCashPanel.add(tempPanel);

		balanceAddressCombo = new JComboBox<>(new String[] { "" });
		comboBoxParentPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		comboBoxParentPanel.add(balanceAddressCombo);
		sendCashPanel.add(comboBoxParentPanel);
		
		JLabel dividerLabel = new JLabel("   ");
		dividerLabel.setFont(new Font("Helvetica", Font.PLAIN, 3));
		sendCashPanel.add(dividerLabel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tempPanel.add(new JLabel("Destination address:"));
		sendCashPanel.add(tempPanel);
		
		destinationAddressField = new JTextField(73);
		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tempPanel.add(destinationAddressField);
		sendCashPanel.add(tempPanel);
				
		dividerLabel = new JLabel("   ");
		dividerLabel.setFont(new Font("Helvetica", Font.PLAIN, 3));
		sendCashPanel.add(dividerLabel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tempPanel.add(new JLabel("Memo (optional):"));
		sendCashPanel.add(tempPanel);
		
		destinationMemoField = new JTextField(73);
		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        tempPanel.add(destinationMemoField);
		sendCashPanel.add(tempPanel);		
		
		dividerLabel = new JLabel("   ");
		dividerLabel.setFont(new Font("Helvetica", Font.PLAIN, 3));
		sendCashPanel.add(dividerLabel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tempPanel.add(new JLabel("Amount to send:"));
		sendCashPanel.add(tempPanel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tempPanel.add(destinationAmountField = new JTextField(13));
		sendCashPanel.add(tempPanel);
		
		dividerLabel = new JLabel("   ");
		dividerLabel.setFont(new Font("Helvetica", Font.PLAIN, 3));
		sendCashPanel.add(dividerLabel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tempPanel.add(sendButton = new JButton("Send   \u27A4\u27A4\u27A4"));
		sendCashPanel.add(tempPanel);

		dividerLabel = new JLabel("   ");
		dividerLabel.setFont(new Font("Helvetica", Font.PLAIN, 28));
		sendCashPanel.add(dividerLabel);
		
		// Build the operation status panel
		operationStatusPanel = new JPanel();
		//this.add(operationStatusPanel, BorderLayout.SOUTH);
		sendCashPanel.add(operationStatusPanel);
		operationStatusPanel.setLayout(new BoxLayout(operationStatusPanel, BoxLayout.Y_AXIS));
		//operationStatusPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
		
		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tempPanel.add(new JLabel("Last operation status: "));
        tempPanel.add(operationStatusLabel = new JLabel("N/A"));
        operationStatusPanel.add(tempPanel);		
		
		dividerLabel = new JLabel("   ");
		dividerLabel.setFont(new Font("Helvetica", Font.PLAIN, 6));
		operationStatusPanel.add(dividerLabel);

		tempPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tempPanel.add(new JLabel("Progress: "));
        tempPanel.add(operationStatusProhgressBar = new JProgressBar(0, 200));
        operationStatusProhgressBar.setPreferredSize(new Dimension(250, 17));
        operationStatusPanel.add(tempPanel);		
        
		dividerLabel = new JLabel("   ");
		dividerLabel.setFont(new Font("Helvetica", Font.PLAIN, 13));
		operationStatusPanel.add(dividerLabel);
		
		// Wire the buttons
		sendButton.addActionListener(new ActionListener() 
		{	
			public void actionPerformed(ActionEvent e) 
			{
				try
			    {
					SendCashPanel.this.sendCash();
				} catch (Exception ex)
				{
					ex.printStackTrace();
					
					String errMessage = "";
					if (ex instanceof WalletCallException)
					{
						errMessage = ((WalletCallException)ex).getMessage().replace(",", ",\n");
					}
					
					JOptionPane.showMessageDialog(
							SendCashPanel.this.getRootPane().getParent(), 
							"An unexpected error occurred when sending cash!\n" + 
							"Please ensure that the ZCaash daemon is running and\n" +
							"parameters are correct. You may again later...\n" +
							errMessage, 
							"Error in sending cash", JOptionPane.ERROR_MESSAGE);
				}
			}
		});

		// Update the balances via timer
		ActionListener alBalancesUpdater = new ActionListener() 
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					long start = System.currentTimeMillis();
					SendCashPanel.this.updateWalletAddressPositiveBalanceComboBox();
					long end = System.currentTimeMillis();
					
					System.out.println("Update of send cash panel balances done in " + (end - start) + "ms." );
				} catch (Exception ex)
				{
					ex.printStackTrace();
					SendCashPanel.this.errorReporter.reportError(ex);
				}
			}
		};
		Timer timerBalancesUpdater = new Timer(30000, alBalancesUpdater);
		timerBalancesUpdater.setInitialDelay(1000);
		timerBalancesUpdater.start();
	}
	
	
	private void sendCash()
		throws WalletCallException, IOException, InterruptedException
	{
		if (balanceAddressCombo.getItemCount() <= 0)
		{
			JOptionPane.showMessageDialog(
				SendCashPanel.this.getRootPane().getParent(), 
				"There are no addresses with a positive balance to send\n" +
				"cash from!", 
				"No funds available", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (this.balanceAddressCombo.getSelectedIndex() <= 0)
		{
			JOptionPane.showMessageDialog(
				SendCashPanel.this.getRootPane().getParent(), 
				"Please select a source address with a current positive\n" +
				"balance to send cash from!", 
				"Please select source address", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String sourceAddress = this.lastAddressBalanceData[this.balanceAddressCombo.getSelectedIndex()][1];
		String destinationAddress = this.destinationAddressField.getText();
		String memo = this.destinationMemoField.getText();
		String amount = this.destinationAmountField.getText();

		// Verify general correctness.
		String errorMessage = null;
		
		if ((sourceAddress == null) || (sourceAddress.trim().length() <= 20))
		{
			errorMessage = "Source address is invalid; it is too short or missing.";
		} else if (sourceAddress.length() > 512)
		{
			errorMessage = "Source address is invalid; it is too long.";
		}
		
		// TODO: full address validation
		if ((destinationAddress == null) || (destinationAddress.trim().length() <= 0))
		{
			errorMessage = "Destination address is invalid; it is missing.";
		} else if (destinationAddress.trim().length() <= 20)
		{
			errorMessage = "Destination address is invalid; it is too short.";
		} else if (destinationAddress.length() > 512)
		{
			errorMessage = "Destination address is invalid; it is too long.";
		}
		
		if ((amount == null) || (amount.trim().length() <= 0))
		{
			errorMessage = "Amount to send is invalid; it is missing.";
		} else 
		{
			try 
			{
				double d = Double.valueOf(amount);
			} catch (NumberFormatException nfe)
			{
				errorMessage = "Amount to send is invalid; it is not a number.";				
			}
		}

		if (errorMessage != null)
		{
			JOptionPane.showMessageDialog(
				SendCashPanel.this.getRootPane().getParent(), 
				errorMessage, "Sending parameters are incorrect", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		// Call the wallet send method
		operationStatusID = this.clientCaller.sendCash(sourceAddress, destinationAddress, amount, memo);
		
		// Disable controls after send
		sendButton.setEnabled(false);
		balanceAddressCombo.setEnabled(false);
		destinationAddressField.setEnabled(false);
		destinationAmountField.setEnabled(false);
		destinationMemoField.setEnabled(false);
		
		// Start a timer to update the progress of the operation
		operationStatusCounter = 0;
		operationStatusTimer = new Timer(2000, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				try
				{
					if (clientCaller.isSendingOperationComplete(operationStatusID))
					{
						if (clientCaller.isCompletedOperationSuccessful(operationStatusID))
						{
							operationStatusLabel.setText(
								"<html><span style=\"color:green;font-weight:bold\">SUCCESSFUL</span></html>");
						} else
						{
							String errorMessage = clientCaller.getOperationFinalErrorMessage(operationStatusID); 
							operationStatusLabel.setText(
								"<html><span style=\"color:red;font-weight:bold\">ERROR: " + errorMessage + "</span></html>");

						}
						
						// Restore controls etc.
						operationStatusCounter = 0;
						operationStatusID      = null;
						operationStatusTimer.stop();
						operationStatusTimer = null;
						operationStatusProhgressBar.setValue(0);
						
						sendButton.setEnabled(true);
						balanceAddressCombo.setEnabled(true);
						destinationAddressField.setEnabled(true);
						destinationAmountField.setEnabled(true);
						destinationMemoField.setEnabled(true);
					} else
					{
						// Update the progress
						operationStatusLabel.setText(
							"<html><span style=\"color:orange;font-weight:bold\">IN PROGRESS</span></html>");
						operationStatusCounter += 2;
						int progress = 0;
						if (operationStatusCounter <= 100)
						{
							progress = operationStatusCounter;
						} else
						{
							progress = 100 + (((operationStatusCounter - 100) * 6) / 10);
						}
						operationStatusProhgressBar.setValue(progress);
					}
					
					SendCashPanel.this.repaint();
				} catch (Exception ex)
				{
					ex.printStackTrace();
					SendCashPanel.this.errorReporter.reportError(ex);
				}
			}
		});
		operationStatusTimer.setInitialDelay(0);
		operationStatusTimer.start();
	}

		
	private void updateWalletAddressPositiveBalanceComboBox()
		throws WalletCallException, IOException, InterruptedException
	{
		String[][] newAddressBalanceData = this.getAddressPositiveBalanceDataFromWallet();
		lastAddressBalanceData = newAddressBalanceData;
		
		comboBoxItems = new String[lastAddressBalanceData.length];
		for (int i = 0; i < lastAddressBalanceData.length; i++)
		{
			comboBoxItems[i] = Double.valueOf(lastAddressBalanceData[i][0]).toString().toString()  + 
					           " - " + lastAddressBalanceData[i][1];
		}
		
		int selectedIndex = balanceAddressCombo.getSelectedIndex();
		boolean isEnabled = balanceAddressCombo.isEnabled();
		this.comboBoxParentPanel.remove(balanceAddressCombo);
		balanceAddressCombo = new JComboBox<>(comboBoxItems);
		comboBoxParentPanel.add(balanceAddressCombo);
		if (balanceAddressCombo.getItemCount() > 0)
		{
			balanceAddressCombo.setSelectedIndex(selectedIndex);
		}
		balanceAddressCombo.setEnabled(isEnabled);

		this.validate();
		this.repaint();
	}


	private String[][] getAddressPositiveBalanceDataFromWallet()
		throws WalletCallException, IOException, InterruptedException
	{
		// Z Addresses - they are OK
		String[] zAddresses = clientCaller.getWalletZAddresses();
		
		// T Addresses created by GUI only
		// TODO: What if wallet is changed - stored addresses are invalid?!!
		String[] tAddresses = AddressesPanel.getCreatedAndStoredTAddresses();
		Set<String> tStoredAddressSet = new HashSet<>();
		for (String address : tAddresses)
		{
			tStoredAddressSet.add(address);
		}
		
		// T addresses with unspent outputs (even if not GUI created)...
		String[] tAddressesWithUnspentOuts = this.clientCaller.getWalletPublicAddressesWithUnspentOutputs();
		Set<String> tAddressSetWithUnspentOuts = new HashSet<>();
		for (String address : tAddressesWithUnspentOuts)
		{
			tAddressSetWithUnspentOuts.add(address);
		}
		
		// Combine all known T addresses
		Set<String> tAddressesCombined = new HashSet<>();
		tAddressesCombined.addAll(tStoredAddressSet);
		tAddressesCombined.addAll(tAddressSetWithUnspentOuts);
		
		String[][] tempAddressBalances = new String[zAddresses.length + tAddressesCombined.size()][];
		
		int count = 0;

		for (String address : tAddressesCombined)
		{
			String balance = this.clientCaller.getBalanceForAddress(address);
			if (Double.valueOf(balance) > 0)
			{
				tempAddressBalances[count++] = new String[] 
				{  
					balance, address
				};
			}
		}
		
		for (String address : zAddresses)
		{
			String balance = this.clientCaller.getBalanceForAddress(address);
			if (Double.valueOf(balance) > 0)
			{
				tempAddressBalances[count++] = new String[] 
				{  
					balance, address
				};
			}
		}

		String[][] addressBalances = new String[count][];
		System.arraycopy(tempAddressBalances, 0, addressBalances, 0, count);
		
		return addressBalances;
	}
	
	
}
