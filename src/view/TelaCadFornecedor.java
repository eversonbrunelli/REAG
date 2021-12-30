/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

/**
 *
 * @author eversonbrunelli-fit
 */

import java.sql.*;
import dal.Conexao;
import java.awt.Color;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;
public class TelaCadFornecedor extends javax.swing.JFrame {
    
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form TelaCadFornecedor
     */
    public TelaCadFornecedor() {
        initComponents();
        conexao = Conexao.conector();
        getContentPane().setBackground(Color.GRAY);
    }
    
    //Metodo Consulta

    private void consultar() {

        String sql = "select * from fornecedor where fornecedor like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, jTextFieldPesquisaFor.getText() + "%");
            rs = pst.executeQuery();

            // A linha abaixo usa a bibilioteca rs2.jar para preencher a tabela
            jTableFornecedor.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
    
    //Metodo para setar os campos do formulario com o campo da tabela selecionado
    public void setar_campos(){
    
        int setar = jTableFornecedor.getSelectedRow();
        jTextFieldIdFornecedorTelaCadFor.setText(jTableFornecedor.getModel().getValueAt(setar, 0).toString());
        jTextFieldFornecedorTelaCadFor.setText(jTableFornecedor.getModel().getValueAt(setar, 1).toString());
        // A linha abaixo desabilita o botão adicionar
        jButtonCreateFor.setEnabled(false);
    }
    
       private void adicionar() {

        String sql = "insert into fornecedor(fornecedor) values (?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, jTextFieldFornecedorTelaCadFor.getText());
            //Validação dos campos obrigatórios
            if (((jTextFieldFornecedorTelaCadFor.getText().isEmpty()))) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {

                //A linha abaixo insere usuario no banco com os dados fornecidos
                //A estrutura abaixo é testar e confirma a insercao
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Fornecedor adicionado com sucesso!");
                    jTextFieldIdFornecedorTelaCadFor.setText(null);
                    jTextFieldFornecedorTelaCadFor.setText(null);

                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);

        }
    }
       private void alterar() {

        String sql = "update fornecedor set fornecedor = ? where idfornecedor = ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, jTextFieldFornecedorTelaCadFor.getText());
            pst.setString(2, jTextFieldIdFornecedorTelaCadFor.getText());
            if (((jTextFieldFornecedorTelaCadFor.getText().isEmpty()))) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {

                //A linha abaixo altera usuario no banco com os dados fornecidos
                //A estrutura abaixo é testar e confirma a alteracao
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Dados alterados com sucesso!");
                    jTextFieldIdFornecedorTelaCadFor.setText(null);
                    jTextFieldFornecedorTelaCadFor.setText(null);
                    jButtonCreateFor.setEnabled(true);

                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
          //Metodo Deletar

    public void deletar() {
        //Estrutura confirma remoção
        int confirma = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir o fornecedor?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from fornecedor where idfornecedor = ?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, jTextFieldIdFornecedorTelaCadFor.getText());
                int removido = pst.executeUpdate();
                if (removido > 0) {
                    JOptionPane.showMessageDialog(null, "Fornecedor excluído com sucesso!");
                    jTextFieldIdFornecedorTelaCadFor.setText(null);
                    jTextFieldFornecedorTelaCadFor.setText(null);

                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, e);
            }
        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextFieldPesquisaFor = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableFornecedor = new javax.swing.JTable();
        jTextFieldIdFornecedorTelaCadFor = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldFornecedorTelaCadFor = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButtonCreateFor = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro de Fornecedor");
        setResizable(false);

        jTextFieldPesquisaFor.setBackground(new java.awt.Color(204, 204, 204));
        jTextFieldPesquisaFor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPesquisaForKeyReleased(evt);
            }
        });

        jLabel5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/viewIcones/search_new_3.png"))); // NOI18N

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/viewIcones/fornecedor_new.png"))); // NOI18N

        jTableFornecedor.setBackground(new java.awt.Color(204, 204, 204));
        jTableFornecedor.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTableFornecedor.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTableFornecedor.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableFornecedorMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableFornecedor);

        jTextFieldIdFornecedorTelaCadFor.setEditable(false);
        jTextFieldIdFornecedorTelaCadFor.setBackground(new java.awt.Color(204, 204, 204));

        jLabel2.setText("Código");

        jTextFieldFornecedorTelaCadFor.setBackground(new java.awt.Color(204, 204, 204));

        jLabel3.setText("Fornecedor");

        jButtonCreateFor.setText("Adicionar");
        jButtonCreateFor.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonCreateFor.setPreferredSize(new java.awt.Dimension(80, 80));
        jButtonCreateFor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateForActionPerformed(evt);
            }
        });

        jButton3.setText("Alterar");
        jButton3.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton3.setPreferredSize(new java.awt.Dimension(80, 80));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Deletar");
        jButton4.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton4.setPreferredSize(new java.awt.Dimension(80, 80));
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextFieldPesquisaFor, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(36, 36, 36)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldFornecedorTelaCadFor, javax.swing.GroupLayout.PREFERRED_SIZE, 305, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldIdFornecedorTelaCadFor, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(7, 7, 7))
            .addGroup(layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addComponent(jButtonCreateFor, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton3, jButton4, jButtonCreateFor});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel5)
                        .addGap(38, 38, 38)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addComponent(jTextFieldPesquisaFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldIdFornecedorTelaCadFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldFornecedorTelaCadFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButtonCreateFor, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(34, 34, 34))
        );

        setSize(new java.awt.Dimension(586, 461));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldPesquisaForKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPesquisaForKeyReleased
        consultar();
    }//GEN-LAST:event_jTextFieldPesquisaForKeyReleased

    private void jTableFornecedorMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableFornecedorMouseClicked
        setar_campos();
    }//GEN-LAST:event_jTableFornecedorMouseClicked

    private void jButtonCreateForActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateForActionPerformed
        adicionar();
    }//GEN-LAST:event_jButtonCreateForActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        alterar();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        deletar();
    }//GEN-LAST:event_jButton4ActionPerformed

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
            java.util.logging.Logger.getLogger(TelaCadFornecedor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaCadFornecedor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaCadFornecedor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaCadFornecedor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaCadFornecedor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButtonCreateFor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableFornecedor;
    private javax.swing.JTextField jTextFieldFornecedorTelaCadFor;
    private javax.swing.JTextField jTextFieldIdFornecedorTelaCadFor;
    private javax.swing.JTextField jTextFieldPesquisaFor;
    // End of variables declaration//GEN-END:variables
}
