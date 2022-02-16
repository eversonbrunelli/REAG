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
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.view.JasperViewer;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.HashMap;

/**
 *
 * @author eversonbrunelli-fit
 */
public class TelaEntrada extends javax.swing.JFrame {

    Connection conexao = null;
    PreparedStatement pst = null;
    ResultSet rs = null;

    /**
     * Creates new form TelaEntrada
     */
    public TelaEntrada() {
        initComponents();
        conexao = Conexao.conector();
        getContentPane().setBackground(Color.GRAY);
        pesquisa_analista();


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

    //Pesquisa os produtos no campo através do nome e apresenta na tabela
    private void pesquisa_produto() {

        String sql = "select idproduto as Id, produto as Produto, coderp as CodErp, classificacao as Classificacao, armazenamento as Armazenamento, est_min as EstMin from produto where produto like ?";
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
        jTextFieldClassificacao.setText(jTableProdutos.getModel().getValueAt(setapro, 3).toString());
        jTextFieldCodErp.setText(jTableProdutos.getModel().getValueAt(setapro, 2).toString());
        jTextFieldArmazenamento.setText(jTableProdutos.getModel().getValueAt(setapro, 4).toString());
        jTextFieldEstMin.setText(jTableProdutos.getModel().getValueAt(setapro, 5).toString());

    }

    //Pesquisa os fornecedores e apresenta na tabela
    private void pesquisa_marca() {

        String sql = "select idmarca as Id, marca as Marca from marca where marca like ?";
        try {
            pst = conexao.prepareStatement(sql);
            pst.setString(1, jTextFieldPesquisaMar.getText() + "%");
            rs = pst.executeQuery();
            jTableMarca.setModel(DbUtils.resultSetToTableModel(rs));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

//Seta o fornecedor selecionado da tabela no campo ID
    private void seta_fornecedor() {

        int setafor = jTableMarca.getSelectedRow();
        jTextFieldMarca.setText(jTableMarca.getModel().getValueAt(setafor, 1).toString());

    }

    //Método de entrada de reagente
    private void entrada_reagente() {

        String sql = "Insert into entrada(datarec,lotefor,validade,idproduto,classificacao,produto,quantidade,marca,usuario,coderp,armazenamento,est_min) values (?,?,?,?,?,?,?,?,?,?,?,?)";

        try {
            pst = conexao.prepareStatement(sql);
            Date data = new Date();
            SimpleDateFormat formatar = new SimpleDateFormat("yyyy/MM/dd");
            String dataFormatada = formatar.format(data);
            pst.setString(1, dataFormatada);
            pst.setString(2, jTextFieldLoteFor.getText());
            //conversao de string to date para mysql 18/03/1983 1983-03-18
            String dia = jTextFieldValidade.getText().substring(0, 2);
            String mes = jTextFieldValidade.getText().substring(3, 5);
            String ano = jTextFieldValidade.getText().substring(6);
            String datavalidade = ano + "-" + mes + "-" + dia;
            pst.setString(3, datavalidade);
            pst.setString(4, jTextFieldIdProduto.getText());
            pst.setString(5, jTextFieldClassificacao.getText());
            pst.setString(6, jTextFieldProduto.getText());
            pst.setString(7, jTextFieldQtd.getText());
            pst.setString(8, jTextFieldMarca.getText());
            pst.setString(9, jComboBoxAnalista.getSelectedItem().toString());
            pst.setString(10, jTextFieldCodErp.getText());
            pst.setString(11, jTextFieldArmazenamento.getText());
            pst.setString(12, jTextFieldEstMin.getText());

            //Verifica campos obrigatórios
            if ((((((jTextFieldLoteFor.getText().isEmpty()) || (jTextFieldValidade.getText().isEmpty())) || (jTextFieldProduto.getText().isEmpty())) || (jTextFieldMarca.getText().isEmpty())) || (jTextFieldLoteFor.getText().isEmpty())) || (jTextFieldQtd.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos da tela!");
            } else {

                int adicionado = pst.executeUpdate();

                if (adicionado > 0) {

                    JOptionPane.showMessageDialog(null, "Entrada realizada com sucesso!");

                    //Captura o valor da quantidade salva na ultima entrada
                    String sql2 = "select identrada,quantidade,idproduto,est_min,lotefor,validade from entrada order by identrada desc limit 1";

                    pst = conexao.prepareStatement(sql2);
                    rs = pst.executeQuery();

                    if (rs.next()) {

                        String qtd_atual = rs.getString("quantidade");
                        String idproduto = rs.getString("idproduto");
                        String estmin = rs.getString("est_min");
                        String lote = rs.getString("lotefor");
                        String validade = rs.getString("validade");
                        String identrada = rs.getString("identrada");
                        //JOptionPane.showMessageDialog(null, identrada);

                        int qtd_atual_new = Integer.parseInt(qtd_atual);
                        //JOptionPane.showMessageDialog(null, qtd_atual_new);

                        //Verifica estoque existente e Captura o valor do estoque atual do produto caso exista
                        String sql3 = "select est from estoque where idproduto = ? and lotefor = ?";

                        pst = conexao.prepareStatement(sql3);
                        pst.setString(1, idproduto);
                        pst.setString(2, lote);
                        rs = pst.executeQuery();

                        if (rs.first()) {

                            String est_atual = rs.getString("est");
                            int est_atual_new = Integer.parseInt(est_atual);
                            //JOptionPane.showMessageDialog(null, est_atual_new);

                            //Calculo de estoque atual + entrada
                            int total_est = est_atual_new + qtd_atual_new;
                            String total_est_new = Integer.toString(total_est);
                            //JOptionPane.showMessageDialog(null, total_est_new);

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

                                //Impressao atraves de caixa de dialogo da entrada gerada
                                int confirma = JOptionPane.showConfirmDialog(null, "Deseja imprimir a entrada?", "Atenção!", JOptionPane.YES_NO_OPTION);
                                if (confirma == JOptionPane.YES_OPTION) {
                                    // Imprimir relatório com o framework JasperReports        
                                    try {
                                        // usando a classe HashMap para criar um filtro
                                        HashMap filtro = new HashMap();
                                        filtro.put("identrada",
                                                Integer.parseInt(identrada));

                                        //usando a classe Jasperprint para preparar a impressão    
                                        JasperPrint imprime = JasperFillManager.fillReport("\\\\URLdoRelatorio.jasper", filtro, conexao);
                                        //A linha abaixo exibe o relatório através da classe JasperVieWer
                                        JasperViewer.viewReport(imprime, false);

                                    } catch (Exception e) {
                                        JOptionPane.showMessageDialog(null, e);
                                    }
                                }

                                jTextFieldLoteFor.setText(null);
                                jTextFieldValidade.setText(null);
                                jTextFieldIdProduto.setText(null);
                                jTextFieldClassificacao.setText(null);
                                jTextFieldProduto.setText(null);
                                jTextFieldQtd.setText(null);
                                jTextFieldMarca.setText(null);
                                jTextFieldCodErp.setText(null);
                                jTextFieldArmazenamento.setText(null);
                                jTextFieldEstMin.setText(null);
                                jTextFieldNrEntrada.setText(null);
                                jTextFieldDtRec.setText(null);
                            }

                        } else {

                            String sql5 = "Insert into estoque (idproduto,est_min,est,lotefor,validade) values (?,?,?,?,?)";

                            pst = conexao.prepareStatement(sql5);
                            pst.setString(1, idproduto);
                            pst.setString(2, estmin);
                            pst.setString(3, qtd_atual);
                            pst.setString(4, lote);
                            pst.setString(5, validade);

                            int inserido = pst.executeUpdate();

                            if (inserido > 0) {

                                JOptionPane.showMessageDialog(null, "Estoque ajustado com sucesso!");
                                
                                //Impressao atraves de caixa de dialogo da entrada gerada
                                int confirma = JOptionPane.showConfirmDialog(null, "Deseja imprimir a entrada?", "Atenção!", JOptionPane.YES_NO_OPTION);
                                if (confirma == JOptionPane.YES_OPTION) {
                                    // Imprimir relatório com o framework JasperReports        
                                    try {
                                        // usando a classe HashMap para criar um filtro
                                        HashMap filtro = new HashMap();
                                        filtro.put("identrada",
                                                Integer.parseInt(identrada));

                                        //usando a classe Jasperprint para preparar a impressão    
                                        JasperPrint imprime = JasperFillManager.fillReport("\\\\URLdoRelatorio.jasper", filtro, conexao);
                                        //A linha abaixo exibe o relatório através da classe JasperVieWer
                                        JasperViewer.viewReport(imprime, false);

                                    } catch (Exception e) {
                                        JOptionPane.showMessageDialog(null, e);
                                    }
                                }
                                

                                jTextFieldLoteFor.setText(null);
                                jTextFieldValidade.setText(null);
                                jTextFieldIdProduto.setText(null);
                                jTextFieldClassificacao.setText(null);
                                jTextFieldProduto.setText(null);
                                jTextFieldQtd.setText(null);
                                jTextFieldMarca.setText(null);
                                jTextFieldCodErp.setText(null);
                                jTextFieldArmazenamento.setText(null);
                                jTextFieldEstMin.setText(null);
                                jTextFieldNrEntrada.setText(null);
                                jTextFieldDtRec.setText(null);

                            }

                        }
                    }

                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    //Método para pesquisa Entrada
    private void pesquisa_entrada() {

        //Cria caixa de entrada jOptionPane
        String num_ent = JOptionPane.showInputDialog("Número da Entrada");
        String sql = "select * from entrada where identrada = " + num_ent;

        try {
            pst = conexao.prepareStatement(sql);
            rs = pst.executeQuery();

            if (rs.next()) {

                jTextFieldNrEntrada.setText(rs.getString(1));
                jTextFieldDtRec.setText(rs.getString(2));
                String dia_rec = rs.getString(2).substring(8, 10);
                String mes_rec = rs.getString(2).substring(5, 7);
                String ano_rec = rs.getString(2).substring(0, 4);
                String datarec = dia_rec + "/" + mes_rec + "/" + ano_rec;
                jTextFieldDtRec.setText(datarec);
                jTextFieldLoteFor.setText(rs.getString(3));
                String dia_val = rs.getString(4).substring(8, 10);
                String mes_val = rs.getString(4).substring(5, 7);
                String ano_val = rs.getString(4).substring(0, 4);
                String dataval = dia_val + "/" + mes_val + "/" + ano_val;
                jTextFieldValidade.setText(dataval);
                jTextFieldIdProduto.setText(rs.getString(5));
                jTextFieldClassificacao.setText(rs.getString(6));
                jTextFieldProduto.setText(rs.getString(7));
                jTextFieldQtd.setText(rs.getString(8));
                jTextFieldMarca.setText(rs.getString(9));
                jComboBoxAnalista.setSelectedItem(rs.getString(10));
                jTextFieldCodErp.setText(rs.getString(11));
                jTextFieldArmazenamento.setText(rs.getString(12));
                jTextFieldEstMin.setText(rs.getString(13));
                jButtonAdiciona.setEnabled(false);
                jTextFieldQtd.setEnabled(false);
                jTextFieldPesquisaProd.setEnabled(false);
                jTextFieldValidade.setEnabled(false);
                jTextFieldLoteFor.setEnabled(false);

            } else {

                JOptionPane.showMessageDialog(null, "Entrada não encontrada!");

                jTextFieldLoteFor.setText(null);
                jTextFieldValidade.setText(null);
                jTextFieldIdProduto.setText(null);
                jTextFieldClassificacao.setText(null);
                jTextFieldProduto.setText(null);
                jTextFieldQtd.setText(null);
                jTextFieldMarca.setText(null);
                jTextFieldCodErp.setText(null);
                jTextFieldArmazenamento.setText(null);
                jTextFieldEstMin.setText(null);
                jTextFieldNrEntrada.setText(null);
                jTextFieldDtRec.setText(null);
                jButtonAdiciona.setEnabled(true);

            }

        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(null, e);
        }

    }

    private void altera_entrada() {

        String sql = "update entrada set datarec = ?,lotefor = ?,validade = ?,idproduto = ?,classificacao = ?,produto = ?,marca = ?,usuario = ?,coderp = ?,armazenamento = ?,est_min = ? where identrada = ?";

        try {
            pst = conexao.prepareStatement(sql);
            Date data = new Date();
            SimpleDateFormat formatar = new SimpleDateFormat("yyyy/MM/dd");
            String dataFormatada = formatar.format(data);
            pst.setString(1, dataFormatada);
            pst.setString(2, jTextFieldLoteFor.getText());
            //conversao de string to date para mysql 18/03/1983 1983-03-18
            String dia = jTextFieldValidade.getText().substring(0, 2);
            String mes = jTextFieldValidade.getText().substring(3, 5);
            String ano = jTextFieldValidade.getText().substring(6);
            String datavalidade = ano + "-" + mes + "-" + dia;
            pst.setString(3, datavalidade);
            pst.setString(4, jTextFieldIdProduto.getText());
            pst.setString(5, jTextFieldClassificacao.getText());
            pst.setString(6, jTextFieldProduto.getText());
            pst.setString(7, jTextFieldMarca.getText());
            pst.setString(8, jComboBoxAnalista.getSelectedItem().toString());
            pst.setString(9, jTextFieldCodErp.getText());
            pst.setString(10, jTextFieldArmazenamento.getText());
            pst.setString(11, jTextFieldEstMin.getText());
            pst.setString(12, jTextFieldNrEntrada.getText());

            //Verifica campos obrigatórios
            if ((((((jTextFieldLoteFor.getText().isEmpty()) || (jTextFieldValidade.getText().isEmpty())) || (jTextFieldProduto.getText().isEmpty())) || (jTextFieldMarca.getText().isEmpty())) || (jTextFieldLoteFor.getText().isEmpty())) || (jTextFieldQtd.getText().isEmpty())) {
                JOptionPane.showMessageDialog(null, "Preencha todos os campos da tela!");
            } else {

                int adicionado = pst.executeUpdate();

                if (adicionado > 0) {

                    JOptionPane.showMessageDialog(null, "Dados alterados com sucesso!");

                    jTextFieldLoteFor.setText(null);
                    jTextFieldValidade.setText(null);
                    jTextFieldIdProduto.setText(null);
                    jTextFieldClassificacao.setText(null);
                    jTextFieldProduto.setText(null);
                    jTextFieldQtd.setText(null);
                    jTextFieldMarca.setText(null);
                    jTextFieldCodErp.setText(null);
                    jTextFieldArmazenamento.setText(null);
                    jTextFieldEstMin.setText(null);
                    jTextFieldNrEntrada.setText(null);
                    jTextFieldDtRec.setText(null);
                    jButtonAdiciona.setEnabled(true);
                    jTextFieldQtd.setEditable(true);

                }
            }

        } catch (SQLException | HeadlessException e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }

    private void deleta_entrada() {

        int confirma = JOptionPane.showConfirmDialog(null, "Deseja realmente excluir a entrada?", "Atenção", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {

            String qtd_ent_del = jTextFieldQtd.getText();
            String idproduto_del = jTextFieldIdProduto.getText();
            String estmin_del = jTextFieldEstMin.getText();
            String lote_del = jTextFieldLoteFor.getText();

            int qtd_ent_del_new = Integer.parseInt(qtd_ent_del);
            //JOptionPane.showMessageDialog(null, qtd_ent_del_new);

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

                    //Calculo de estoque atual - entrada deletada
                    int total_est_del = est_atual_del_new - qtd_ent_del_new;
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

                        String sql3 = "delete from entrada where identrada = ?";

                        pst = conexao.prepareStatement(sql3);
                        pst.setString(1, jTextFieldNrEntrada.getText());

                        int removido = pst.executeUpdate();
                        if (removido > 0) {

                            JOptionPane.showMessageDialog(null, "Entrada excluída com sucesso!");
                            jTextFieldLoteFor.setText(null);
                            jTextFieldValidade.setText(null);
                            jTextFieldIdProduto.setText(null);
                            jTextFieldClassificacao.setText(null);
                            jTextFieldProduto.setText(null);
                            jTextFieldQtd.setText(null);
                            jTextFieldMarca.setText(null);
                            jTextFieldCodErp.setText(null);
                            jTextFieldArmazenamento.setText(null);
                            jTextFieldEstMin.setText(null);
                            jTextFieldNrEntrada.setText(null);
                            jTextFieldDtRec.setText(null);
                            jButtonAdiciona.setEnabled(true);
                            jTextFieldQtd.setEditable(true);

                        }

                    }

                }

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, e);
            }

        }

    }

    //Metodo para imprimir entrada gerada
    private void imprime_entrada() {

        int confirma = JOptionPane.showConfirmDialog(null, "Deseja imprimir a entrada?", "Atenção!", JOptionPane.YES_NO_OPTION);
        if (confirma == JOptionPane.YES_OPTION) {
            // Imprimir relatório com o framework JasperReports        
            try {
                // usando a classe HashMap para criar um filtro
                HashMap filtro = new HashMap();
                filtro.put("identrada",
                        Integer.parseInt(jTextFieldNrEntrada.getText()));

                //usando a classe Jasperprint para preparar a impressão    
                JasperPrint imprime = JasperFillManager.fillReport("\\\\URLdoRelatorio.jasper", filtro, conexao);
                //A linha abaixo exibe o relatório através da classe JasperVieWer
                JasperViewer.viewReport(imprime, false);

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

        jPanel1 = new javax.swing.JPanel();
        jTextFieldNrEntrada = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jTextFieldIdProduto = new javax.swing.JTextField();
        jTextFieldClassificacao = new javax.swing.JTextField();
        jTextFieldProduto = new javax.swing.JTextField();
        jTextFieldCodErp = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableProdutos = new javax.swing.JTable();
        jLabel15 = new javax.swing.JLabel();
        jTextFieldPesquisaProd = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jTextFieldEstMin = new javax.swing.JTextField();
        jTextFieldArmazenamento = new javax.swing.JTextField();
        jTextFieldMarca = new javax.swing.JTextField();
        jTextFieldPesquisaMar = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTableMarca = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jTextFieldLoteFor = new javax.swing.JTextField();
        jTextFieldValidade = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jComboBoxAnalista = new javax.swing.JComboBox();
        jTextFieldDtRec = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jTextFieldQtd = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButtonAdiciona = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Entrada de Reagentes");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(153, 153, 153));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jTextFieldNrEntrada.setEditable(false);
        jTextFieldNrEntrada.setBackground(new java.awt.Color(204, 204, 204));

        jLabel1.setText("N° Entrada");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextFieldNrEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextFieldNrEntrada, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jTextFieldIdProduto.setEditable(false);
        jTextFieldIdProduto.setBackground(new java.awt.Color(204, 204, 204));

        jTextFieldClassificacao.setEditable(false);
        jTextFieldClassificacao.setBackground(new java.awt.Color(204, 204, 204));

        jTextFieldProduto.setEditable(false);
        jTextFieldProduto.setBackground(new java.awt.Color(204, 204, 204));

        jTextFieldCodErp.setEditable(false);
        jTextFieldCodErp.setBackground(new java.awt.Color(204, 204, 204));
        jTextFieldCodErp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldCodErpActionPerformed(evt);
            }
        });

        jTableProdutos.setBackground(new java.awt.Color(204, 204, 204));
        jTableProdutos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Produto", "CodErp", "Classificacao", "Marca", "Armazenamento", "EstMin"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTableProdutos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableProdutosMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTableProdutos);

        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/viewIcones/search_new_3.png"))); // NOI18N
        jLabel15.setPreferredSize(new java.awt.Dimension(80, 80));

        jTextFieldPesquisaProd.setBackground(new java.awt.Color(204, 204, 204));
        jTextFieldPesquisaProd.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPesquisaProdKeyReleased(evt);
            }
        });

        jLabel2.setText("Código REAG");

        jLabel3.setText("Classificação");

        jLabel4.setText("CódigoERP");

        jLabel5.setText("Descrição");

        jLabel7.setText("Armazenamento");

        jLabel8.setText("Est/Min");

        jTextFieldEstMin.setEditable(false);
        jTextFieldEstMin.setBackground(new java.awt.Color(204, 204, 204));

        jTextFieldArmazenamento.setEditable(false);
        jTextFieldArmazenamento.setBackground(new java.awt.Color(204, 204, 204));

        jTextFieldMarca.setEditable(false);
        jTextFieldMarca.setBackground(new java.awt.Color(204, 204, 204));

        jTextFieldPesquisaMar.setBackground(new java.awt.Color(204, 204, 204));
        jTextFieldPesquisaMar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextFieldPesquisaMarKeyReleased(evt);
            }
        });

        jLabel21.setIcon(new javax.swing.ImageIcon(getClass().getResource("/viewIcones/search_new_3.png"))); // NOI18N
        jLabel21.setPreferredSize(new java.awt.Dimension(80, 80));

        jTableMarca.setBackground(new java.awt.Color(204, 204, 204));
        jTableMarca.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jTableMarca.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableMarcaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTableMarca);

        jLabel9.setText("Marca");

        jTextFieldLoteFor.setBackground(new java.awt.Color(204, 204, 204));

        jTextFieldValidade.setBackground(new java.awt.Color(204, 204, 204));

        jLabel10.setText("Lote");

        jLabel11.setText("Validade");

        jLabel12.setText("Analista");

        jComboBoxAnalista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxAnalistaActionPerformed(evt);
            }
        });

        jTextFieldDtRec.setEditable(false);
        jTextFieldDtRec.setBackground(new java.awt.Color(204, 204, 204));

        jLabel13.setText("Data Recebimento");

        jLabel14.setForeground(new java.awt.Color(153, 0, 0));
        jLabel14.setText("Quantidade");

        jTextFieldQtd.setBackground(new java.awt.Color(204, 204, 204));

        jButton2.setText("Pesquisar");
        jButton2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton2.setPreferredSize(new java.awt.Dimension(80, 80));
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton5.setText("Imprimir");
        jButton5.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButton5.setPreferredSize(new java.awt.Dimension(80, 80));
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButtonAdiciona.setText("Adicionar");
        jButtonAdiciona.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jButtonAdiciona.setPreferredSize(new java.awt.Dimension(80, 80));
        jButtonAdiciona.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAdicionaActionPerformed(evt);
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
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel14)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jTextFieldQtd, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel11)
                                    .addComponent(jLabel12))
                                .addGap(29, 29, 29)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jComboBoxAnalista, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldValidade, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jTextFieldLoteFor, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 182, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 542, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(3, 3, 3)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel9)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jTextFieldMarca, javax.swing.GroupLayout.PREFERRED_SIZE, 376, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel10)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                        .addGap(18, 18, 18))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel13)
                                        .addGap(18, 18, 18)
                                        .addComponent(jTextFieldDtRec, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(48, 48, 48)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel3)
                                            .addComponent(jLabel2)
                                            .addComponent(jLabel5)
                                            .addComponent(jLabel4)
                                            .addComponent(jLabel7)
                                            .addComponent(jLabel8))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextFieldProduto)
                                            .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                    .addComponent(jTextFieldIdProduto, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jTextFieldCodErp, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jTextFieldClassificacao, javax.swing.GroupLayout.PREFERRED_SIZE, 149, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jTextFieldArmazenamento, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                    .addComponent(jTextFieldEstMin, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 0, Short.MAX_VALUE))))
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(0, 15, Short.MAX_VALUE)
                                        .addComponent(jTextFieldPesquisaProd, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(63, 63, 63))))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jTextFieldPesquisaMar, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(57, 57, 57)
                                .addComponent(jButtonAdiciona, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(66, 66, 66)
                                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(73, 73, 73)
                                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(66, 66, 66)
                                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(88, 88, 88)))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton2, jButton3, jButton4, jButton5, jButtonAdiciona});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(9, 9, 9)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jLabel2)
                                            .addComponent(jTextFieldIdProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jTextFieldDtRec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel13))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jTextFieldClassificacao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel3))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jTextFieldProduto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel5))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jTextFieldCodErp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel4)))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                            .addComponent(jTextFieldMarca, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel9)))))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jTextFieldPesquisaMar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextFieldArmazenamento, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jTextFieldEstMin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(jLabel10)
                                    .addComponent(jTextFieldLoteFor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(16, 16, 16))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(37, 37, 37)
                                .addComponent(jTextFieldPesquisaProd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(50, 50, 50))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(106, 106, 106)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel11)
                            .addComponent(jTextFieldValidade, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jComboBoxAnalista, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addGap(14, 14, 14)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldQtd, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel14))))
                .addGap(97, 97, 97)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButtonAdiciona, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(64, Short.MAX_VALUE))
        );

        setSize(new java.awt.Dimension(1004, 680));
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextFieldCodErpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldCodErpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldCodErpActionPerformed

    private void jTableProdutosMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableProdutosMouseClicked
        seta_produto();
    }//GEN-LAST:event_jTableProdutosMouseClicked

    private void jTextFieldPesquisaProdKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPesquisaProdKeyReleased
        // Pesquisa produtos pelo nome
        pesquisa_produto();
    }//GEN-LAST:event_jTextFieldPesquisaProdKeyReleased

    private void jTextFieldPesquisaMarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextFieldPesquisaMarKeyReleased
        pesquisa_marca();
    }//GEN-LAST:event_jTextFieldPesquisaMarKeyReleased

    private void jTableMarcaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableMarcaMouseClicked
        seta_fornecedor();
    }//GEN-LAST:event_jTableMarcaMouseClicked

    private void jComboBoxAnalistaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxAnalistaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxAnalistaActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

        pesquisa_entrada();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        imprime_entrada();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButtonAdicionaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAdicionaActionPerformed
        entrada_reagente();
    }//GEN-LAST:event_jButtonAdicionaActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        altera_entrada();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        deleta_entrada();
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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaEntrada.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaEntrada().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButtonAdiciona;
    private javax.swing.JComboBox jComboBoxAnalista;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTableMarca;
    private javax.swing.JTable jTableProdutos;
    private javax.swing.JTextField jTextFieldArmazenamento;
    private javax.swing.JTextField jTextFieldClassificacao;
    private javax.swing.JTextField jTextFieldCodErp;
    private javax.swing.JTextField jTextFieldDtRec;
    private javax.swing.JTextField jTextFieldEstMin;
    private javax.swing.JTextField jTextFieldIdProduto;
    private javax.swing.JTextField jTextFieldLoteFor;
    private javax.swing.JTextField jTextFieldMarca;
    private javax.swing.JTextField jTextFieldNrEntrada;
    private javax.swing.JTextField jTextFieldPesquisaMar;
    private javax.swing.JTextField jTextFieldPesquisaProd;
    private javax.swing.JTextField jTextFieldProduto;
    private javax.swing.JTextField jTextFieldQtd;
    private javax.swing.JTextField jTextFieldValidade;
    // End of variables declaration//GEN-END:variables
}
