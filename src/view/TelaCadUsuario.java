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

public class TelaCadUsuario extends javax.swing.JFrame {
    
    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form TelaCadUsuario
     */
    public TelaCadUsuario() {
        initComponents();
        conexao = Conexao.conector();
        getContentPane().setBackground(Color.GRAY);
    }
    //Metodo Consulta

    private void consultar() {

        String sql = "select * from usuario where usuario like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, jTextFieldPesquisaTelaCadUsu.getText() + "%");
            rs = pst.executeQuery();

            // A linha abaixo usa a bibilioteca rs2.jar para preencher a tabela
            jTableTelaCadUsu.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }
    
     //Metodo para setar os campos do formulario com o campo da tabela selecionado
    public void setar_campos(){
    
        int setar = jTableTelaCadUsu.getSelectedRow();
        jTextFieldIdTelaCadUsu.setText(jTableTelaCadUsu.getModel().getValueAt(setar, 0).toString());
        jTextFieldUsuarioTelaCadUsu.setText(jTableTelaCadUsu.getModel().getValueAt(setar, 1).toString());
        jPasswordFieldSenhaTelaCadUsu.setText(jTableTelaCadUsu.getModel().getValueAt(setar, 2).toString());
        jComboBoxPerfil.setSelectedItem(jTableTelaCadUsu.getModel().getValueAt(setar, 3).toString());
        // A linha abaixo desabilita o botão adicionar
        jButtonCreateTelaCadUsu.setEnabled(false);
    }

//Metodo Adicionar
    private void adicionar() {

        String sql = "insert into usuario(usuario, senha, perfil) values (?, ?, ?)";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, jTextFieldUsuarioTelaCadUsu.getText());
            pst.setString(2, jPasswordFieldSenhaTelaCadUsu.getText());
            pst.setString(3, jComboBoxPerfil.getSelectedItem().toString());
            //Validação dos campos obrigatórios
            if (((jTextFieldUsuarioTelaCadUsu.getText().isEmpty())) || (jPasswordFieldSenhaTelaCadUsu.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {

                //A linha abaixo insere usuario no banco com os dados fornecidos
                //A estrutura abaixo é testar e confirma a insercao
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Usuário adicionado com sucesso!");
                    jTextFieldIdTelaCadUsu.setText(null);
                    jTextFieldUsuarioTelaCadUsu.setText(null);
                    jPasswordFieldSenhaTelaCadUsu.setText(null);

                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Usuario e/ou codigo ja cadastrado!");
            jTextFieldIdTelaCadUsu.setText(null);
            jTextFieldUsuarioTelaCadUsu.setText(null);
            jPasswordFieldSenhaTelaCadUsu.setText(null);

        }
    }
//Metodo Alterar

    private void alterar() {

        String sql = "update usuario set usuario = ?, senha = ?, perfil = ? where idusuario = ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, jTextFieldUsuarioTelaCadUsu.getText());
            pst.setString(2, jPasswordFieldSenhaTelaCadUsu.getText());
            pst.setString(3, jComboBoxPerfil.getSelectedItem().toString());
            pst.setString(4, jTextFieldIdTelaCadUsu.getText());
            if (((jTextFieldIdTelaCadUsu.getText().isEmpty()) || (jTextFieldUsuarioTelaCadUsu.getText().isEmpty())) || (jPasswordFieldSenhaTelaCadUsu.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos obrigatórios!");
            } else {

                //A linha abaixo altera usuario no banco com os dados fornecidos
                //A estrutura abaixo é testar e confirma a alteracao
                int adicionado = pst.executeUpdate();
                if (adicionado > 0) {
                    JOptionPane.showMessageDialog(null, "Dados alterados com sucesso!");
                    jTextFieldIdTelaCadUsu.setText(null);
                    jTextFieldUsuarioTelaCadUsu.setText(null);
                    jPasswordFieldSenhaTelaCadUsu.setText(null);
                    jButtonCreateTelaCadUsu.setEnabled(true);

                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
//Metodo Deletar

    public void deletar() {
        //Estrutura confirma remoção
        int confirma = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir o usuário?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            String sql = "delete from usuario where idusuario = ?";
            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, jTextFieldIdTelaCadUsu.getText());
                int removido = pst.executeUpdate();
                if (removido > 0) {
                    JOptionPane.showMessageDialog(null, "Usuário excluído com sucesso!");
                    jTextFieldIdTelaCadUsu.setText(null);
                    jTextFieldUsuarioTelaCadUsu.setText(null);
                    jPasswordFieldSenhaTelaCadUsu.setText(null);
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

        jTextFieldPesquisaTelaCadUsu = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableTelaCadUsu = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldIdTelaCadUsu = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldUsuarioTelaCadUsu = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jPasswordFieldSenhaTelaCadUsu = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        jComboBoxPerfil = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jButtonCreateTelaCadUsu = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Cadastro de Usuário");
        setBackground(new java.awt.Color(204, 204, 204));
        setResizable(false);

        jTextFieldPesquisaTelaCadUsu.setBackground(new java.awt.Color(204, 204, 204));
        jTextFieldPesquisaTelaCadUsu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPesquisaTelaCadUsuKeyReleased(evt);
            }
        });

        jLabel7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/viewIcones/search_new_3.png"))); // NOI18N

        jTableTelaCadUsu.setBackground(new java.awt.Color(204, 204, 204));
        jTableTelaCadUsu.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jTableTelaCadUsu.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTableTelaCadUsu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableTelaCadUsuMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableTelaCadUsu);

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/viewIcones/usuario_new.png"))); // NOI18N

        jTextFieldIdTelaCadUsu.setEditable(false);
        jTextFieldIdTelaCadUsu.setBackground(new java.awt.Color(204, 204, 204));

        jLabel2.setText("Código");

        jTextFieldUsuarioTelaCadUsu.setBackground(new java.awt.Color(204, 204, 204));

        jLabel3.setText("Usuário");

        jPasswordFieldSenhaTelaCadUsu.setBackground(new java.awt.Color(204, 204, 204));

        jLabel4.setText("Senha");

        jComboBoxPerfil.setBackground(new java.awt.Color(204, 204, 204));
        jComboBoxPerfil.setFont(new java.awt.Font("Comic Sans MS", 0, 12)); // NOI18N
        jComboBoxPerfil.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "admin", "analista", "user" }));

        jLabel5.setText("Perfil");

        jButtonCreateTelaCadUsu.setText("Adicionar");
        jButtonCreateTelaCadUsu.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonCreateTelaCadUsu.setPreferredSize(new java.awt.Dimension(80, 80));
        jButtonCreateTelaCadUsu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateTelaCadUsuActionPerformed(evt);
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
                    .addComponent(jTextFieldPesquisaTelaCadUsu, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(56, 56, 56)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextFieldUsuarioTelaCadUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldIdTelaCadUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPasswordFieldSenhaTelaCadUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jComboBoxPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(106, 106, 106))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(16, 16, 16))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonCreateTelaCadUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(56, 56, 56)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(58, 58, 58)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(53, 53, 53))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton3, jButton4, jButtonCreateTelaCadUsu});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addComponent(jTextFieldPesquisaTelaCadUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldIdTelaCadUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldUsuarioTelaCadUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addGap(22, 22, 22)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jPasswordFieldSenhaTelaCadUsu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBoxPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(43, 43, 43)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 58, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButtonCreateTelaCadUsu, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(27, 27, 27))
        );

        setSize(new java.awt.Dimension(569, 526));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldPesquisaTelaCadUsuKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPesquisaTelaCadUsuKeyReleased
        consultar();
    }//GEN-LAST:event_jTextFieldPesquisaTelaCadUsuKeyReleased

    private void jTableTelaCadUsuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableTelaCadUsuMouseClicked
        setar_campos();
    }//GEN-LAST:event_jTableTelaCadUsuMouseClicked

    private void jButtonCreateTelaCadUsuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateTelaCadUsuActionPerformed
        adicionar();
    }//GEN-LAST:event_jButtonCreateTelaCadUsuActionPerformed

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
            java.util.logging.Logger.getLogger(TelaCadUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaCadUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaCadUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaCadUsuario.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaCadUsuario().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButtonCreateTelaCadUsu;
    private javax.swing.JComboBox jComboBoxPerfil;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPasswordField jPasswordFieldSenhaTelaCadUsu;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTableTelaCadUsu;
    private javax.swing.JTextField jTextFieldIdTelaCadUsu;
    private javax.swing.JTextField jTextFieldPesquisaTelaCadUsu;
    private javax.swing.JTextField jTextFieldUsuarioTelaCadUsu;
    // End of variables declaration//GEN-END:variables
}
