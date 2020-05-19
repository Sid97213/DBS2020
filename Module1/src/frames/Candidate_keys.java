/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package frames;
import frames.Start;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author sheyril
 */

class SuperKeys
{
    static Set<String> super_key = new HashSet<String>();
    void func(String[] arr, int n, Dictionary vis, String data[], String left[], String right[])
    {
        // int no_of_attributes = n;
        int num_fd = left.length;
        int ch = 1;
        for (int j = 0; j < n; j++)
        {
            if ((int)vis.get(arr[j]) == 0)
            {
                ch = 0;
                break;
            }
        }
        if (ch == 1) return;
        for (int i = 0; i < num_fd; i++)
        {
            int check = 1;
            for (int j = 0; j < n; j++)
            {
                if ((int)vis.get(arr[j]) == 0)
                {
                    check = 0;
                    break;
                }
            }
            if (check == 1) 
            {
                break;
            }
            check = 1;
            for (int j=0; j<left[i].length(); j++) {
                String tmp = Character.toString(left[i].charAt(j));
                if ((int)vis.get(tmp) == 0)
                    {
                        check = 0;
                        break;
                    }
            }
            if (check == 1)
            {
                int rec = 0;
                for (int j=0; j<right[i].length(); j++) {
                    String tmp = Character.toString(right[i].charAt(j));
                    if ((int)vis.get(tmp) == 0) rec = 1;
                            vis.put(tmp, 1);
                }
                if (rec == 1) func(arr, n, vis, data, left, right);
            }
        }
        int chh = 1;
        for (int j = 0; j < n; j++)
        {
            if ((int)vis.get(arr[j]) == 0)
            {
                chh = 0;
                break;
            }
        }
        if (chh == 1)
        {
            String tmp = "";
            for (int i = 0; i < data.length; i++)
            {   
                tmp += data[i];
            }
            super_key.add(tmp);
        }   
    }
}

class HighestNForm
{
    static int bcnf = 1;
    static int nf3 = 1;
    static int nf2 = 1;
    static int nf1 = 1;

    static boolean anagrams(String lhs, String key) 
    { 
        int n1 = lhs.length(); 
        int n2 = key.length(); 

        if (n1 != n2) 
            return false; 
  
        // Sorting both strings 
        char[] ch1 = lhs.toCharArray(); 
        char[] ch2 = key.toCharArray(); 
        Arrays.sort(ch1); 
        Arrays.sort(ch2); 
  
        // Comparing sorted strings 
        for (int i = 0; i < n1; i++) 
            if (ch1[i] != ch2[i]) 
                return false; 
  
        return true; 
    }

    void highest_nf(String attributes[], int n, String left[], String right[], List<String> arr)
    {
        int num_attr = n;
        Dictionary prime_attr = new Hashtable();
        for (int i = 0; i < num_attr; i++){
            prime_attr.put(attributes[i], 0);
        }
        for(String key : arr){
            for (int i=0; i<key.length(); i++) {
                prime_attr.put(Character.toString(key.charAt(i)),1);
            }
        }

        //check for 2nf
        // presence of partial dependency
        int pd=0;
        for (int i=0; i<right.length; i++) 
        {
            String rhs = right[i];
            for (int j=0; j<rhs.length(); j++) 
            {
                if ((int)prime_attr.get(Character.toString(rhs.charAt(j)))==0)          //only need to check if RHS is non-candidate
                {              
                    String lhs = left[i];
                    for(String key : arr)
                    {
                        Dictionary dic3 = new Hashtable();      //candidate keys
                        Dictionary dic4 = new Hashtable();      //lhs
                        for (int z = 0; z < num_attr; z++) 
                        {
                            dic3.put(attributes[z], 0); 
                            dic4.put(attributes[z], 0); 
                        }
                        for (int k=0; k<key.length(); k++) {
                           dic3.put(Character.toString(key.charAt(k)),1);
                        }
                        for (int k=0; k<lhs.length(); k++) {
                           dic4.put(Character.toString(lhs.charAt(k)),1);
                        }
                        int flag1 = 0;
                        int flag2 = 0;
                        for (int k = 0; k < num_attr; k++)
                        {
                            if ((int)dic3.get(attributes[k]) == 0 && (int)dic4.get(attributes[k]) == 1) flag1 = 1;
                            if ((int)dic3.get(attributes[k]) == 1 && (int)dic4.get(attributes[k]) == 0) flag2 = 1;
                        }

                        if (flag1 == 0 && flag2 == 1)
                        { 
                            pd = 1;
                            break;
                        }
                    }               
                }if (pd == 1) break; 
            }if (pd == 1) break;

        }
        if (pd == 1)
        {
            nf2=0;
//            return;
        }
//        hnf=2;

        //check for 3nf
        // for X->Y, either X is a super key or Y is a prime attribute
        for (int i=0; i<right.length; i++) {
            String lhs = left[i];
            int flag1=0;
            for(String key : SuperKeys.super_key)
            {
                if (anagrams(lhs, key))
                {
                    flag1 = 1;
                    break;
                }
            }
            if (flag1==1) continue;                 //first condition is true
            int flag2 = 0;
            String rhs = right[i]; 
            for (int j=0; j<rhs.length(); j++)
            {
                if ((int)prime_attr.get(Character.toString(rhs.charAt(j))) == 0)
                {
                    flag2 = 1;
                    break;
                }
            }
            if (flag2 == 1)                         //second condition false
            {
                nf3 = 0;
                break;
                // return;
            }
        }
//        hnf=3;

        //check for bcnf
        // for every X->Y, X should be a super key
        for (int i = 0; i < right.length; i++)
        {
            String lhs = left[i];
            int flag1 = 0;
            for(String key : SuperKeys.super_key)
            {
                if (anagrams(lhs, key))
                {
                    flag1 = 1;
                    break;
                }
            }
            if (flag1 == 0)
            {
                bcnf = 0;
                // break;
                return;
            }
        }
//        hnf = -1;
//        return;
    }
}

public class Candidate_keys extends javax.swing.JFrame {

    /**
     * Creates new form Candidate_keys
     */
    static int n=1;
//    Static String rel = Start.
    public Candidate_keys() {
        initComponents();
        
        DefaultTableModel dtm = new DefaultTableModel(0, 0);
        String header[] = new String[] { "Candidate Keys" };
        // add header in table model     
        dtm.setColumnIdentifiers(header);
        //set model into the table object
        jTable1.setModel(dtm);
        
        for(String s: Start.arr) {
            dtm.addRow(new Object[] {s});
        }
    }
//    public Candidate_keys(Relations r) {
//        initComponents();
//        DefaultTableModel dtm = new DefaultTableModel(0, 0);
//        String header[] = new String[] { "Candidate Keys" };
//        // add header in table model     
//        dtm.setColumnIdentifiers(header);
//        //set model into the table object
//        jTable1.setModel(dtm);
//        
//        for(String s: Start.arr) {
//            dtm.addRow(new Object[] {s});
//        }
//    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Lucida Grande", 0, 15)); // NOI18N
        jLabel1.setText("Candidate Keys for this relation are:");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("Check Highest Normal form");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Back");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(84, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
                .addGap(111, 111, 111))
            .addGroup(layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(48, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    static void combinationUtil(String[] arr, int n, int r, int index, String data[], int i, String left[], String right[]) 
    {
        if (index == r) 
        {
            Dictionary vis = new Hashtable(); 
            for (int j = 0; j < n; j++)
            {
                vis.put(arr[j], 0);
            }
            for (int j = 0; j < r; j++) vis.put(data[j], 1); 
            SuperKeys c = new SuperKeys();
            c.func(arr, n, vis, data, left, right);
            return; 
        }
        if (i >= n) 
            return; 
        data[index] = (arr[i]); 
        combinationUtil(arr, n, r, index + 1, data, i + 1, left, right); 
        combinationUtil(arr, n, r, index, data, i + 1, left, right); 
    }
    
    static void Combinations(String[] arr, int n, int r, String left[], String right[]) 
    {
        String data[] = new String[r];
        combinationUtil(arr, n, r, 0, data, 0, left, right); 
    }
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        String rel = Start.relation;
        String fds = Start.fd;
//        JOptionPane.showMessageDialog(null,"CP1" + rel +" " + fds );
        ArrayList attr = new ArrayList();
        StringTokenizer str = new StringTokenizer(rel, "{,}");
        while (str.hasMoreTokens()){
            attr.add(str.nextToken());      
        } 
//        JOptionPane.showMessageDialog(null,"CP2");
        String[] attributes = new String[attr.size()];
        for (int i=0; i<attr.size(); i++) {
            attributes[i] = (String)attr.get(i);
        }
//        JOptionPane.showMessageDialog(null,"CP3");
        StringTokenizer st1 = new StringTokenizer(fds, "{,}"); 
        ArrayList left = new ArrayList();
        ArrayList right = new ArrayList();
        while (st1.hasMoreTokens()){
          String st2 = st1.nextToken();
          StringTokenizer st3 = new StringTokenizer(st2, "->");
          left.add(st3.nextToken());
          right.add(st3.nextToken());
        } 

        int num_fd = left.size();
        String[] left_attr = new String[num_fd];
        String[] right_attr = new String[num_fd];
        for (int i=0; i<num_fd; i++) {
            left_attr[i]=(String)left.get(i);
            right_attr[i]=(String)right.get(i);
        }

        //computing super keys of the relation
        String temp = "";
        for (int i = 0; i < attr.size(); i++)
        {
            temp += attributes[i];
            Combinations(attributes, attr.size(), i + 1, left_attr, right_attr);
        }
//        JOptionPane.showMessageDialog(null,"done prepro");
        HighestNForm nf = new HighestNForm();
        nf.highest_nf(attributes, attr.size(), left_attr, right_attr, Start.arr);
//        n = nf.hnf;
        System.out.println("nf1 " + nf.nf1 + " nf2 " + nf.nf2 + " nf3 " + nf.nf3 + " bcnf " + nf.bcnf);
        if ((nf.nf2==1)&&(nf.nf3==0)&&(nf.bcnf==0)){
            n=2;
        }
        else if ((nf.nf2==1)&&(nf.nf3==1)&&(nf.bcnf==0)){
            n=3;
        }
        else if ((nf.nf2==1)&&(nf.nf3==1)&&(nf.bcnf==1)){
            n=-1;
        }
        else if ((nf.nf2==0)&&(nf.nf3==0)&&(nf.bcnf==0)){
            n=1;
        }
        
        HighestNF highNF = new HighestNF();
        highNF.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        Start st = new Start();
        st.setVisible(true);
        dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Candidate_keys.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Candidate_keys.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Candidate_keys.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Candidate_keys.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Candidate_keys().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables
}
