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
//import net.sf.jasperreports.engine.JasperFillManager;
//import net.sf.jasperreports.engine.JasperPrint;
//import net.sf.jasperreports.view.JasperViewer;
import java.util.Date;
import java.text.SimpleDateFormat;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.SimpleEmail;

/**
 *
 * @author eversonbrunelli-fit
 */
public class TelaSaida extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;
    private String produto;

    /**
     * Creates new form TelaSaida
     */
    public TelaSaida() {
        initComponents();
        conexao = Conexao.conector();
        getContentPane().setBackground(Color.GRAY);
        pesquisa_analista();
    }

    //Pesquisa os produtos no campo através do nome e apresenta na tabela
    private void pesquisa_produto() {

        String sql = "select a.idproduto as idproduto,a.produto as produto,a.coderp as coderp,b.est_min as est_min,b.est as est,b.lotefor as lote,b.validade as validade from produto as a, estoque as b where a.idproduto=b.idproduto and a.produto like ? and b.est !=0 order by b.validade";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, jTextFieldPesquisaProd.getText() + "%");
            rs = pst.executeQuery();
            jTableProdutos.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    //Seta o produto selecionado da tabela no campo ID
    private void seta_produto() {

        int setapro = jTableProdutos.getSelectedRow();
        jTextFieldIdProduto.setText(jTableProdutos.getModel().getValueAt(setapro, 0).toString());
        jTextFieldProduto.setText(jTableProdutos.getModel().getValueAt(setapro, 1).toString());
        jTextFieldCodErp.setText(jTableProdutos.getModel().getValueAt(setapro, 2).toString());
        jTextFieldEstMin.setText(jTableProdutos.getModel().getValueAt(setapro, 3).toString());
        jTextFieldLote.setText(jTableProdutos.getModel().getValueAt(setapro, 5).toString());
        jTextFieldValidade.setText(jTableProdutos.getModel().getValueAt(setapro, 6).toString());

    }

    //Metodo pesquisa os analistas no banco de dados e populariza a caixa de combinação
    private void pesquisa_analista() {

        String sql = "Select * from usuario where perfil='analista'";
        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();
            rs.first();
            do {
                jComboBoxAnalista.addItem(rs.getString("usuario"));
            } while (rs.next());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void envia_email_estoque() {

        String meuEmail = "seu@email";
        String minhaSenha = "senha";

        SimpleEmail email = new SimpleEmail();
        email.setHostName("servidor@email");
        email.setSslSmtpPort("465");
        email.setAuthenticator(new DefaultAuthenticator(meuEmail, minhaSenha));
        email.setSSLOnConnect(true);
        email.setStartTLSRequired(true);
        email.setStartTLSEnabled(true);
        email.setSSLOnConnect(true);

        try {

            email.setFrom(meuEmail);
            email.setSubject("Estoque Minimo Atingido");
            email.setMsg(produto);
            email.addTo("lista@email");
            email.send();
            JOptionPane.showMessageDialog(null, "Email de alerta de estoque mínimo enviado!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saida_reagente() {

        //Verifica estoque e compara com a quantidade de saida
        String sql6 = "select est from estoque where idproduto = ? and lotefor = ?";

        try {

            pst = conexao.prepareStatement(sql6);
            pst.setString(1, jTextFieldIdProduto.getText());
            pst.setString(2, jTextFieldLote.getText());
            rs = pst.executeQuery();

            if (rs.first()) {

                String est_pro = rs.getString("est");
                int est_pro_new = Integer.parseInt(est_pro);
                //JOptionPane.showMessageDialog(null, est_pro_new);
                String qtd = jTextFieldQtd.getText();
                int qtd_new = Integer.parseInt(qtd);
                //JOptionPane.showMessageDialog(null, qtd_new);

                if (est_pro_new < qtd_new) {

                    JOptionPane.showMessageDialog(null, "Estoque menor que a saída informada");
                } else {
                    String sql = "Insert into saida(dtsaida,idproduto,produto,usuario,est_min,quantidade,coderp,lotefor,validade,tipo) values (?,?,?,?,?,?,?,?,?,?)";

                    pst = conexao.prepareStatement(sql);
                    Date data = new Date();
                    SimpleDateFormat formatar = new SimpleDateFormat("yyyy/MM/dd");
                    String dataFormatada = formatar.format(data);
                    pst.setString(1, dataFormatada);
                    pst.setString(2, jTextFieldIdProduto.getText());
                    pst.setString(3, jTextFieldProduto.getText());
                    pst.setString(4, jComboBoxAnalista.getSelectedItem().toString());
                    pst.setString(5, jTextFieldEstMin.getText());
                    pst.setString(6, jTextFieldQtd.getText());
                    pst.setString(7, jTextFieldCodErp.getText());
                    pst.setString(8, jTextFieldLote.getText());
                    pst.setString(9, jTextFieldValidade.getText());
                    pst.setString(10, jComboBoxTipo.getSelectedItem().toString());

                    int adicionado = pst.executeUpdate();

                    if (adicionado > 0) {

                        JOptionPane.showMessageDialog(null, "Saída realizada com sucesso!");

                        //Captura o valor da quantidade salva na ultima saida
                        String sql2 = "select quantidade,idproduto,est_min,lotefor,validade from saida order by idsaida desc limit 1";
                        pst = conexao.prepareStatement(sql2);
                        rs = pst.executeQuery();

                        if (rs.next()) {

                            String qtd_atual = rs.getString("quantidade");
                            String idproduto = rs.getString("idproduto");
                            String estmin = rs.getString("est_min");
                            String lote = rs.getString("lotefor");
                            //String validade = rs.getString("validade");

                            int qtd_atual_new = Integer.parseInt(qtd_atual);

                            //Captura o valor do estoque atual do produto
                            String sql3 = "select est from estoque where idproduto = ? and lotefor = ?";

                            pst = conexao.prepareStatement(sql3);
                            pst.setString(1, idproduto);
                            pst.setString(2, lote);
                            rs = pst.executeQuery();

                            if (rs.first()) {

                                String est_atual = rs.getString("est");
                                int est_atual_new = Integer.parseInt(est_atual);
                            //JOptionPane.showMessageDialog(null, est_atual);

                                //Calculo de estoque atual + entrada
                                int total_est = est_atual_new - qtd_atual_new;
                                String total_est_new = Integer.toString(total_est);

                                //Atualizar os valores de estoque no banco
                                String sql4 = "update estoque set est = ?,est_min = ? where idproduto = ? and lotefor = ?";

                                pst = conexao.prepareStatement(sql4);
                                pst.setString(1, total_est_new);
                                pst.setString(2, estmin);
                                pst.setString(3, idproduto);
                                pst.setString(4, lote);

                                int ajustado = pst.executeUpdate();

                                if (ajustado > 0) {

                                    JOptionPane.showMessageDialog(null, "Estoque ajustado com sucesso!");

                                    int estmin_new = Integer.parseInt(estmin);

                                    String sql5 = "select sum(est) as estoque_total from estoque where idproduto = ?";

                                    pst = conexao.prepareStatement(sql5);
                                    pst.setString(1, idproduto);
                                    rs = pst.executeQuery();

                                    if (rs.first()) {

                                        String total_est_geral = rs.getString("estoque_total");
                                        int total_est_geral_new = Integer.parseInt(total_est_geral);

                                        if (estmin_new >= total_est_geral_new) {

                                            produto = jTextFieldProduto.getText();
                                            envia_email_estoque();
                                        }
                                    }
                                    jTextFieldIdProduto.setText(null);
                                    jTextFieldProduto.setText(null);
                                    jTextFieldQtd.setText(null);
                                    jTextFieldEstMin.setText(null);
                                    jTextFieldCodErp.setText(null);
                                    jTextFieldLote.setText(null);
                                    jTextFieldValidade.setText(null);
                                }
                            }

                        }
                    }
                }
            }
        } catch (Exception e) {
        }

    }

    //Método para pesquisa Saída
    private void pesquisa_saida() {

        //Cria caixa de entrada jOptionPane
        String num_saida = JOptionPane.showInputDialog("Número da Saída");
        String sql = "select * from saida where idsaida = " + num_saida;

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {

                jTextFieldNrSaida.setText(rs.getString(1));
                jTextFieldDtSaida.setText(rs.getString(2));
                String dia_rec = rs.getString(2).substring(8, 10);
                String mes_rec = rs.getString(2).substring(5, 7);
                String ano_rec = rs.getString(2).substring(0, 4);
                String datarec = dia_rec + "/" + mes_rec + "/" + ano_rec;
                jTextFieldDtSaida.setText(datarec);
                jTextFieldIdProduto.setText(rs.getString(3));
                jTextFieldProduto.setText(rs.getString(4));
                jTextFieldQtd.setText(rs.getString(7));
                jComboBoxAnalista.setSelectedItem(rs.getString(8));
                jTextFieldCodErp.setText(rs.getString(5));
                jTextFieldEstMin.setText(rs.getString(6));
                jTextFieldLote.setText(rs.getString(9));
                jTextFieldValidade.setText(rs.getString(10));
                jComboBoxTipo.setSelectedItem(rs.getString(11));
                jButtonAdiciona.setEnabled(false);
                jTextFieldQtd.setEditable(false);
                jTextFieldPesquisaProd.setEnabled(false);
                jTableProdutos.setEnabled(false);

            } else {

                JOptionPane.showMessageDialog(null, "Saída não encontrada!");

                jTextFieldIdProduto.setText(null);
                jTextFieldProduto.setText(null);
                jTextFieldQtd.setText(null);
                jTextFieldCodErp.setText(null);
                jTextFieldEstMin.setText(null);
                jTextFieldNrSaida.setText(null);
                jTextFieldDtSaida.setText(null);
                jTextFieldPesquisaProd.setText(null);
                jButtonAdiciona.setEnabled(true);
                jTextFieldQtd.setEditable(true);
                jTextFieldPesquisaProd.setEnabled(true);
                jTableProdutos.setEnabled(true);

            }

        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    private void deleta_saida() {

        int confirma = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir a saída?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {

            String qtd_ent_del = jTextFieldQtd.getText();
            String idproduto_del = jTextFieldIdProduto.getText();
            String estmin_del = jTextFieldEstMin.getText();
            String lote_del = jTextFieldLote.getText();
            int qtd_ent_del_new = Integer.parseInt(qtd_ent_del);
            JOptionPane.showMessageDialog(null, qtd_ent_del_new);

            String sql = "select * from estoque where idproduto = ? and lotefor = ?";

            try {
                pst = conexao.prepareStatement(sql);
                pst.setString(1, idproduto_del);
                pst.setString(2, lote_del);
                rs = pst.executeQuery();

                if (rs.next()) {

                    String est_atual_del = rs.getString("est");
                    int est_atual_del_new = Integer.parseInt(est_atual_del);
                    //JOptionPane.showMessageDialog(null, est_atual_del_new);

                    //Calculo de estoque atual + saida deletada
                    int total_est_del = est_atual_del_new + qtd_ent_del_new;
                    String total_est_del_new = Integer.toString(total_est_del);
                    //JOptionPane.showMessageDialog(null, total_est_del_new);

                    //Atualizar os valores de estoque no banco
                    String sql2 = "update estoque set est = ?,est_min = ? where idproduto = ? and lotefor = ?";

                    pst = conexao.prepareStatement(sql2);
                    pst.setString(1, total_est_del_new);
                    pst.setString(2, estmin_del);
                    pst.setString(3, idproduto_del);
                    pst.setString(4, lote_del);

                    int ajustado = pst.executeUpdate();

                    if (ajustado > 0) {

                        JOptionPane.showMessageDialog(null, "Estoque ajustado com sucesso!");

                        String sql3 = "delete from saida where idsaida = ?";

                        pst = conexao.prepareStatement(sql3);
                        pst.setString(1, jTextFieldNrSaida.getText());

                        int removido = pst.executeUpdate();
                        if (removido > 0) {

                            JOptionPane.showMessageDialog(null, "Saida excluída com sucesso!");
                            jTextFieldIdProduto.setText(null);
                            jTextFieldProduto.setText(null);
                            jTextFieldQtd.setText(null);
                            jTextFieldCodErp.setText(null);
                            jTextFieldEstMin.setText(null);
                            jTextFieldNrSaida.setText(null);
                            jTextFieldDtSaida.setText(null);
                            jButtonAdiciona.setEnabled(true);
                            jTextFieldQtd.setEditable(true);
                            jTextFieldPesquisaProd.setEnabled(true);

                        }

                    }

                }

            } catch (SQLException e) {
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

        jPanel1 = new javax.swing.JPanel();
        jTextFieldNrSaida = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldPesquisaProd = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableProdutos = new javax.swing.JTable();
        jTextFieldDtSaida = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextFieldIdProduto = new javax.swing.JTextField();
        jTextFieldProduto = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jTextFieldCodErp = new javax.swing.JTextField();
        jTextFieldEstMin = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldQtd = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jComboBoxAnalista = new javax.swing.JComboBox();
        jLabel12 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jTextFieldLote = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jTextFieldValidade = new javax.swing.JTextField();
        jButtonAdiciona = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jComboBoxTipo = new javax.swing.JComboBox();
        jLabel6 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Saída de Reagentes");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTextFieldNrSaida.setEditable(false);
        jTextFieldNrSaida.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setText("N° Saída");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldNrSaida, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldNrSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jTextFieldPesquisaProd.setBackground(new java.awt.Color(204, 204, 204));
        jTextFieldPesquisaProd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPesquisaProdKeyReleased(evt);
            }
        });

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/viewIcones/search_new_3.png"))); // NOI18N
        jLabel15.setPreferredSize(new java.awt.Dimension(80, 80));

        jTableProdutos.setBackground(new java.awt.Color(204, 204, 204));
        jTableProdutos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTableProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableProdutosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTableProdutos);

        jTextFieldDtSaida.setEditable(false);
        jTextFieldDtSaida.setBackground(new java.awt.Color(204, 204, 204));

        jLabel2.setText("Data Saída");

        jTextFieldIdProduto.setEditable(false);
        jTextFieldIdProduto.setBackground(new java.awt.Color(204, 204, 204));

        jTextFieldProduto.setEditable(false);
        jTextFieldProduto.setBackground(new java.awt.Color(204, 204, 204));

        jLabel3.setText("Código REAG");

        jLabel5.setText("Descrição");

        jLabel4.setText("CódigoERP");

        jTextFieldCodErp.setEditable(false);
        jTextFieldCodErp.setBackground(new java.awt.Color(204, 204, 204));
        jTextFieldCodErp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldCodErpActionPerformed(evt);
            }
        });

        jTextFieldEstMin.setEditable(false);
        jTextFieldEstMin.setBackground(new java.awt.Color(204, 204, 204));

        jLabel8.setText("Est/Min");

        jLabel7.setText("Qtd Saída");

        jComboBoxAnalista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxAnalistaActionPerformed(evt);
            }
        });

        jLabel12.setText("Analista");

        jLabel11.setText("Lote");

        jTextFieldLote.setEditable(false);
        jTextFieldLote.setBackground(new java.awt.Color(204, 204, 204));

        jLabel13.setText("Validade");

        jTextFieldValidade.setEditable(false);
        jTextFieldValidade.setBackground(new java.awt.Color(204, 204, 204));

        jButtonAdiciona.setText("Adicionar");
        jButtonAdiciona.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonAdiciona.setPreferredSize(new java.awt.Dimension(80, 80));
        jButtonAdiciona.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAdicionaActionPerformed(evt);
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

        jButton2.setText("Pesquisar");
        jButton2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton2.setPreferredSize(new java.awt.Dimension(80, 80));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jComboBoxTipo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Utilização", "Descarte" }));

        jLabel6.setText("Tipo");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel2)
                        .addGap(18, 18, 18)
                        .addComponent(jTextFieldDtSaida, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(17, 17, 17)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jTextFieldPesquisaProd, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(jScrollPane2)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel7))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jTextFieldProduto)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextFieldIdProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(76, 76, 76)
                                        .addComponent(jLabel6)
                                        .addGap(37, 37, 37)
                                        .addComponent(jComboBoxTipo, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(jTextFieldCodErp, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(72, 72, 72))
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                                    .addComponent(jTextFieldQtd, javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jTextFieldEstMin, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 64, Short.MAX_VALUE))
                                                .addGap(159, 159, 159)))
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel11)
                                            .addComponent(jLabel12)
                                            .addComponent(jLabel13))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextFieldValidade)
                                            .addComponent(jComboBoxAnalista, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(jTextFieldLote)))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addGap(0, 0, Short.MAX_VALUE)
                                        .addComponent(jButtonAdiciona, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(78, 78, 78)
                                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(258, 258, 258)))))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(74, 74, 74))))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton2, jButton4});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldDtSaida, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2))
                        .addGap(31, 31, 31)
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(50, 50, 50)
                        .addComponent(jTextFieldPesquisaProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldIdProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(jComboBoxTipo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldCodErp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel12)
                    .addComponent(jComboBoxAnalista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldEstMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel11)
                    .addComponent(jTextFieldLote, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextFieldQtd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13)
                    .addComponent(jTextFieldValidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButtonAdiciona, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41))
        );

        setSize(new java.awt.Dimension(677, 602));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldPesquisaProdKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPesquisaProdKeyReleased
        // Pesquisa produtos pelo nome
        pesquisa_produto();
    }//GEN-LAST:event_jTextFieldPesquisaProdKeyReleased

    private void jTableProdutosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableProdutosMouseClicked
        seta_produto();
    }//GEN-LAST:event_jTableProdutosMouseClicked

    private void jTextFieldCodErpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldCodErpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldCodErpActionPerformed

    private void jComboBoxAnalistaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxAnalistaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxAnalistaActionPerformed

    private void jButtonAdicionaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAdicionaActionPerformed
        saida_reagente();
    }//GEN-LAST:event_jButtonAdicionaActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        deleta_saida();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        pesquisa_saida();
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
            java.util.logging.Logger.getLogger(TelaSaida.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaSaida.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaSaida.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaSaida.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaSaida().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButtonAdiciona;
    private javax.swing.JComboBox jComboBoxAnalista;
    private javax.swing.JComboBox jComboBoxTipo;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableProdutos;
    private javax.swing.JTextField jTextFieldCodErp;
    private javax.swing.JTextField jTextFieldDtSaida;
    private javax.swing.JTextField jTextFieldEstMin;
    private javax.swing.JTextField jTextFieldIdProduto;
    private javax.swing.JTextField jTextFieldLote;
    private javax.swing.JTextField jTextFieldNrSaida;
    private javax.swing.JTextField jTextFieldPesquisaProd;
    private javax.swing.JTextField jTextFieldProduto;
    private javax.swing.JTextField jTextFieldQtd;
    private javax.swing.JTextField jTextFieldValidade;
    // End of variables declaration//GEN-END:variables
}
