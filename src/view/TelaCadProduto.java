/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package view;

import java.sql.*;
import dal.Conexao;
import java.awt.Color;
import java.awt.HeadlessException;
import javax.swing.JOptionPane;
import net.proteanit.sql.DbUtils;

/**
 *
 * @author eversonbrunelli-fit
 */
public class TelaCadProduto extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form TelaCadPro
     */
    public TelaCadProduto() {
        initComponents();
        conexao = Conexao.conector();
        getContentPane().setBackground(Color.GRAY);

    }

    //Metodo pesquisa avancada
    private void pesquisa_produto() {

        String sql = "select * from produto where produto like ?";
        try {
            pst = conexao.prepareStatement(sql);
            //passando o conteúdo da caixa de pesquisa para o ?
            //atencao ao % que é a continuação da string sql
            pst.setString(1, jTextFieldPesqTelaCadPro.getText() + "%");
            rs = pst.executeQuery();
            // A linha abaixo usa a bibilioteca rs2.jar para preencher a tabela
            jTableProdutosTelaCadPro.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //Metodo Adicionar
    private void adicionar() {

        String sql = "insert into produto(produto, coderp, classificacao, armazenamento, est_min) values (?, ?, ?, ?, ?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, jTextFieldProdutoTelaCadPro.getText());
            pst.setString(2, jTextFieldCodigoerpTelaCadPro.getText());
            pst.setString(3, jComboBoxClassificacao.getSelectedItem().toString());
            pst.setString(4, jComboBoxArmazenamento.getSelectedItem().toString());
            pst.setString(5, jTextFieldEstMin.getText());

            //Validação dos campos obrigatórios
            if (((jTextFieldProdutoTelaCadPro.getText().isEmpty()) || (jTextFieldCodigoerpTelaCadPro.getText().isEmpty())) || (jTextFieldEstMin.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {

                //A estrutura abaixo é testar e confirma a insercao
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Produto cadastrado com sucesso!");

//             
                    jTextFieldProdutoTelaCadPro.setText(null);
                    jTextFieldCodigoerpTelaCadPro.setText(null);
                    jTextFieldEstMin.setText(null);

                }
            }

        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(null, e);

        }

    }

    //Metodo para setar os campos do formulario com o campo da tabela selecionado
    public void setar_campos() {

        int setar = jTableProdutosTelaCadPro.getSelectedRow();
        jTextFieldIDProduto.setText(jTableProdutosTelaCadPro.getModel().getValueAt(setar, 0).toString());
        jTextFieldProdutoTelaCadPro.setText(jTableProdutosTelaCadPro.getModel().getValueAt(setar, 1).toString());
        jTextFieldCodigoerpTelaCadPro.setText(jTableProdutosTelaCadPro.getModel().getValueAt(setar, 2).toString());
        jComboBoxClassificacao.setSelectedItem(jTableProdutosTelaCadPro.getModel().getValueAt(setar, 3).toString());
        jComboBoxArmazenamento.setSelectedItem(jTableProdutosTelaCadPro.getModel().getValueAt(setar, 4).toString());
        jTextFieldEstMin.setText(jTableProdutosTelaCadPro.getModel().getValueAt(setar, 5).toString());
        // A linha abaixo desabilita o botão adicionar
        jButtonCreateTelaCadPro.setEnabled(false);
    }

    //Metodo Alterar
    private void alterar() {

        String sql = "update produto set produto = ?, coderp = ?, classificacao = ?, armazenamento = ?, est_min = ? where idproduto = ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, jTextFieldProdutoTelaCadPro.getText());
            pst.setString(2, jTextFieldCodigoerpTelaCadPro.getText());
            pst.setString(3, jComboBoxClassificacao.getSelectedItem().toString());
            pst.setString(4, jComboBoxArmazenamento.getSelectedItem().toString());
            pst.setString(5, jTextFieldEstMin.getText());
            pst.setString(6, jTextFieldIDProduto.getText());
            if (((jTextFieldProdutoTelaCadPro.getText().isEmpty()) || (jTextFieldCodigoerpTelaCadPro.getText().isEmpty())) || (jTextFieldEstMin.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {

                //A linha abaixo altera usuario no banco com os dados fornecidos
                //A estrutura abaixo é testar e confirma a alteracao
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Dados alterados com sucesso!");
                    
                    String sql2 = "Update estoque set est_min = ? where idproduto = ?";
                    
                    pst = conexao.prepareStatement(sql2);
                    pst.setString(1, jTextFieldEstMin.getText());
                    pst.setString(2, jTextFieldIDProduto.getText());
                    
                    int alterar = pst.executeUpdate();
                    
                    if (alterar > 0) {
                    
                        JOptionPane.showMessageDialog(null, "Estoque mínimo ajustado!");
                    }
                    
                    jTextFieldProdutoTelaCadPro.setText(null);
                    jTextFieldCodigoerpTelaCadPro.setText(null);
                    jTextFieldEstMin.setText(null);
                    jButtonCreateTelaCadPro.setEnabled(true);

                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //Metodo Deletar
    public void deletar() {
        //Estrutura confirma remoção
        int confirma = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir o produto?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from produto where idproduto = ?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, jTextFieldIDProduto.getText());
                int removido = pst.executeUpdate();
                if (removido > 0) {
                    JOptionPane.showMessageDialog(null, "Produto excluído com sucesso!");
                    jTextFieldProdutoTelaCadPro.setText(null);
                    jTextFieldCodigoerpTelaCadPro.setText(null);
                    jTextFieldEstMin.setText(null);
                    jButtonCreateTelaCadPro.setEnabled(true);
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

        jTextFieldPesqTelaCadPro = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableProdutosTelaCadPro = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldCodigoerpTelaCadPro = new javax.swing.JTextField();
        jTextFieldIDProduto = new javax.swing.JTextField();
        jTextFieldProdutoTelaCadPro = new javax.swing.JTextField();
        jComboBoxClassificacao = new javax.swing.JComboBox();
        jComboBoxArmazenamento = new javax.swing.JComboBox();
        jTextFieldEstMin = new javax.swing.JTextField();
        jLabel12 = new javax.swing.JLabel();
        jButtonCreateTelaCadPro = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro de Produto");
        setBackground(new java.awt.Color(204, 204, 204));
        setResizable(false);

        jTextFieldPesqTelaCadPro.setBackground(new java.awt.Color(204, 204, 204));
        jTextFieldPesqTelaCadPro.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPesqTelaCadProKeyReleased(evt);
            }
        });

        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/viewIcones/search_new_3.png"))); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(80, 80));

        jTableProdutosTelaCadPro.setBackground(new java.awt.Color(204, 204, 204));
        jTableProdutosTelaCadPro.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTableProdutosTelaCadPro.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        jTableProdutosTelaCadPro.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTableProdutosTelaCadPro.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableProdutosTelaCadProMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableProdutosTelaCadPro);

        jLabel1.setText("Código REAG");

        jLabel2.setText("Produto");

        jLabel3.setText("Classificação");

        jLabel4.setText("Armazenamento");

        jLabel7.setText("Estoque Mínimo");

        jLabel8.setText("Código ERP");

        jTextFieldCodigoerpTelaCadPro.setBackground(new java.awt.Color(204, 204, 204));

        jTextFieldIDProduto.setEditable(false);
        jTextFieldIDProduto.setBackground(new java.awt.Color(204, 204, 204));

        jTextFieldProdutoTelaCadPro.setBackground(new java.awt.Color(204, 204, 204));

        jComboBoxClassificacao.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "CORROSIVO", "INFLAMAVEL", "TOXICO", "NAO PERIGOSOS" }));

        jComboBoxArmazenamento.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "TEMP AMBIENTE", "REFRIGERACAO" }));

        jTextFieldEstMin.setBackground(new java.awt.Color(204, 204, 204));

        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/viewIcones/produto_new_2.png"))); // NOI18N

        jButtonCreateTelaCadPro.setText("Adicionar");
        jButtonCreateTelaCadPro.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonCreateTelaCadPro.setPreferredSize(new java.awt.Dimension(80, 80));
        jButtonCreateTelaCadPro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateTelaCadProActionPerformed(evt);
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
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldPesqTelaCadPro)
                            .addComponent(jScrollPane1))
                        .addGap(48, 48, 48)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel2)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel1))
                                .addGap(34, 34, 34)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jTextFieldCodigoerpTelaCadPro)
                                        .addComponent(jComboBoxClassificacao, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jComboBoxArmazenamento, 0, 136, Short.MAX_VALUE)
                                        .addComponent(jTextFieldEstMin, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jTextFieldProdutoTelaCadPro, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldIDProduto, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jLabel12))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonCreateTelaCadPro, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(54, 54, 54)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(85, 85, 85))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton3, jButton4, jButtonCreateTelaCadPro});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTextFieldPesqTelaCadPro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextFieldIDProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldCodigoerpTelaCadPro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jTextFieldProdutoTelaCadPro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBoxClassificacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jComboBoxArmazenamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextFieldEstMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButtonCreateTelaCadPro, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(30, 30, 30))
        );

        setSize(new java.awt.Dimension(637, 566));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldPesqTelaCadProKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPesqTelaCadProKeyReleased
        pesquisa_produto();
    }//GEN-LAST:event_jTextFieldPesqTelaCadProKeyReleased

    private void jTableProdutosTelaCadProMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableProdutosTelaCadProMouseClicked
        //Chama metodo setar campos
        setar_campos();
    }//GEN-LAST:event_jTableProdutosTelaCadProMouseClicked

    private void jButtonCreateTelaCadProActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateTelaCadProActionPerformed
        adicionar();
    }//GEN-LAST:event_jButtonCreateTelaCadProActionPerformed

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
            java.util.logging.Logger.getLogger(TelaCadProduto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaCadProduto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaCadProduto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaCadProduto.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaCadProduto().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButtonCreateTelaCadPro;
    private javax.swing.JComboBox jComboBoxArmazenamento;
    private javax.swing.JComboBox jComboBoxClassificacao;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableProdutosTelaCadPro;
    private javax.swing.JTextField jTextFieldCodigoerpTelaCadPro;
    private javax.swing.JTextField jTextFieldEstMin;
    private javax.swing.JTextField jTextFieldIDProduto;
    private javax.swing.JTextField jTextFieldPesqTelaCadPro;
    private javax.swing.JTextField jTextFieldProdutoTelaCadPro;
    // End of variables declaration//GEN-END:variables
}
