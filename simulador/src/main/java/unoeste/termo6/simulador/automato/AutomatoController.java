package unoeste.termo6.simulador.automato;

import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static unoeste.termo6.simulador.automato.Estado.TipoEstado.FINAL;
import static unoeste.termo6.simulador.automato.Estado.TipoEstado.INICIAL_E_FINAL;

public class AutomatoController {
    private static final double RAIO_ESTADO = 25;

    public ToggleButton bt_estado;
    public ToggleButton bt_inicial;
    public ToggleButton bt_inicial_final;
    public ToggleButton bt_final;
    public ToggleButton bt_ligar;
    public Button bt_limpar;
    public Button bt_automatico;
    public Button bt_manual;
    public Button bt_proximo;

    public Pane painel_automato;

    public TextField tf_palavra;
    public Label lb_resultado;

    private ToggleGroup tg_tipo_estado;
    private ToggleGroup tg_acao;

    private final Map<StackPane, Estado> mapaEstados = new HashMap<>();
    private final Map<Ligacao, Transicao> mapaTransicoes = new HashMap<>();
    private final List<Transicao> listaTransicoesModelo = new ArrayList<>();
    private final Set<Integer> idsEmUso = new HashSet<>();

    private Group g_transicoes;
    private Group g_estados;

    private Estado estadoOrigemLigacao = null;
    private Estado estadoInicialAtual = null;
    private double offsetX, offsetY;

    private String botao_azul_padrao;
    private String botao_azul_hover;
    private String botao_azul_selecionado;

    private String botao_verde_padrao;
    private String botao_verde_hover;
    private String botao_verde_selecionado;

    private String botao_vermelho_padrao;
    private String botao_vermelho_hover;

    private String palavra;

    private ArrayList<Estado> atuais_manual;
    private int i_manual;

    @FXML
    public void initialize() {
        tg_tipo_estado = new ToggleGroup();
        bt_estado.setToggleGroup(tg_tipo_estado);
        bt_inicial.setToggleGroup(tg_tipo_estado);
        bt_inicial_final.setToggleGroup(tg_tipo_estado);
        bt_final.setToggleGroup(tg_tipo_estado);
        tg_acao = new ToggleGroup();
        bt_ligar.setToggleGroup(tg_acao);

        tg_tipo_estado.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null)
                tg_acao.selectToggle(null);
        });
        tg_acao.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null)
                tg_tipo_estado.selectToggle(null);
        });
        bt_estado.setSelected(true);

        g_transicoes = new Group();
        g_estados = new Group();
        painel_automato.getChildren().addAll(g_transicoes, g_estados);

        botao_azul_padrao = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                "-fx-border-color: transparent; -fx-border-width: 2px; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);";
        botao_azul_selecionado = "-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                "-fx-border-color: #f1c40f; -fx-border-width: 2px; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(241, 196, 15, 0.6), 10, 0, 0, 0);";
        botao_azul_hover = "-fx-background-color: #4fb8ff; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                "-fx-border-color: transparent; -fx-border-width: 2px; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);";

        botao_verde_padrao = "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                "-fx-border-color: transparent; -fx-border-width: 2px; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);";
        botao_verde_selecionado = "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                "-fx-border-color: #f1c40f; -fx-border-width: 2px; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(241, 196, 15, 0.6), 10, 0, 0, 0);";
        botao_verde_hover = "-fx-background-color: #3ddc83; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                "-fx-border-color: transparent; -fx-border-width: 2px; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);";

        botao_vermelho_padrao = "-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                "-fx-border-color: transparent; -fx-border-width: 2px; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);";
        botao_vermelho_hover = "-fx-background-color: #ff6b6b; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                "-fx-border-color: transparent; -fx-border-width: 2px; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);";

        tg_tipo_estado.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (oldToggle != null) {
                ToggleButton oldBtn = (ToggleButton) oldToggle;
                oldBtn.setStyle(botao_azul_padrao);
            }
            if (newToggle != null) {
                ToggleButton newBtn = (ToggleButton) newToggle;
                newBtn.setStyle(botao_azul_selecionado);
                tg_acao.selectToggle(null);
            } else {
                bt_estado.setStyle(botao_azul_padrao);
                bt_inicial.setStyle(botao_azul_padrao);
                bt_inicial_final.setStyle(botao_azul_padrao);
                bt_final.setStyle(botao_azul_padrao);
            }
        });

        tg_acao.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (oldToggle != null) {
                ToggleButton oldBtn = (ToggleButton) oldToggle;
                if (oldBtn == bt_ligar)
                    oldBtn.setStyle(botao_verde_padrao);
            }
            if (newToggle != null) {
                ToggleButton newBtn = (ToggleButton) newToggle;
                if (newBtn == bt_ligar)
                    newBtn.setStyle(botao_verde_selecionado);
                tg_tipo_estado.selectToggle(null);
            } else
                bt_ligar.setStyle(botao_verde_padrao);
        });
        bt_estado.setSelected(true);
        bt_estado.setStyle(botao_azul_selecionado);
    }

    @FXML
    private void onBotaoHover(MouseEvent event) {
        Object source = event.getSource();
        if (event.getSource() instanceof ToggleButton button) {
            if (!button.isSelected())
                if (button == bt_ligar)
                    button.setStyle(botao_verde_hover);
                else
                    button.setStyle(botao_azul_hover);
        } else if (source instanceof Button button)
            if (button == bt_limpar)
                button.setStyle(botao_vermelho_hover);
            else
                button.setStyle(botao_azul_hover);
    }

    @FXML
    private void onBotaoSair(MouseEvent event) {
        Object source = event.getSource();
        if (event.getSource() instanceof ToggleButton button) {
            if (!button.isSelected())
                if (button == bt_ligar)
                    button.setStyle(botao_verde_padrao);
                else
                    button.setStyle(botao_azul_padrao);
        } else if (source instanceof Button button)
            if (button == bt_limpar)
                button.setStyle(botao_vermelho_padrao);
            else
                button.setStyle(botao_azul_padrao);
    }

    @FXML
    void manipularClickPainel(MouseEvent event) {
        if (event.getTarget() == painel_automato) {
            ToggleButton tipoSelecionado = (ToggleButton) tg_tipo_estado.getSelectedToggle();
            if (tipoSelecionado != null) {
                Estado.TipoEstado tipo = Estado.TipoEstado.NORMAL;
                if (tipoSelecionado == bt_inicial) tipo = Estado.TipoEstado.INICIAL;
                else if (tipoSelecionado == bt_final) tipo = FINAL;
                else if (tipoSelecionado == bt_inicial_final) { // <-- ADICIONE ESTE ELSE IF
                    tipo = INICIAL_E_FINAL;
                }
                criarNovoEstado(event.getX(), event.getY(), tipo);
            }
        }
    }

    private int gerarProximoId() {
        int id = 0;
        while (idsEmUso.contains(id))
            id++;
        return id;
    }

    private void criarNovoEstado(double x, double y, Estado.TipoEstado tipo) {
        // Verifica se o novo estado terá propriedade inicial
        if (tipo == Estado.TipoEstado.INICIAL || tipo == INICIAL_E_FINAL)
            // Se já existe um estado inicial, precisamos "rebaixá-lo"
            if (estadoInicialAtual != null) {
                Estado.TipoEstado novoTipoParaAntigoInicial = estadoInicialAtual.getTipo() == INICIAL_E_FINAL
                        ? FINAL
                        : Estado.TipoEstado.NORMAL;
                mudarTipoEstado(estadoInicialAtual, novoTipoParaAntigoInicial);
            }

        int novoId = gerarProximoId();
        idsEmUso.add(novoId);
        Estado novoEstado = new Estado(novoId, x, y, tipo);

        // Atualiza a referência para o novo estado inicial
        if (novoEstado.getTipo() == Estado.TipoEstado.INICIAL || novoEstado.getTipo() == INICIAL_E_FINAL)
            this.estadoInicialAtual = novoEstado;

        StackPane visualEstado = criarVisualEstado(novoEstado);
        novoEstado.setRepresentacaoVisual(visualEstado);
        mapaEstados.put(visualEstado, novoEstado);
        g_estados.getChildren().add(visualEstado);
        configurarEventosEstado(novoEstado);
    }


    private StackPane criarVisualEstado(Estado estado) {
        StackPane stackPane = new StackPane();
        stackPane.setPrefSize(2 * RAIO_ESTADO, 2 * RAIO_ESTADO);
        Circle circuloPrincipal = new Circle(RAIO_ESTADO, Color.LIGHTYELLOW);
        circuloPrincipal.setStroke(Color.BLACK);
        circuloPrincipal.setStrokeWidth(1.5);
        circuloPrincipal.getStyleClass().add("circulo-principal");
        Text textoId = new Text("q" + estado.getId());
        textoId.setBoundsType(TextBoundsType.VISUAL);
        stackPane.getChildren().addAll(circuloPrincipal, textoId);

        if (estado.getTipo() == FINAL || estado.getTipo() == INICIAL_E_FINAL) {
            Circle circuloFinal = new Circle(RAIO_ESTADO - 5, Color.TRANSPARENT);
            circuloFinal.setStroke(Color.BLACK);
            circuloFinal.setStrokeWidth(1.5);
            stackPane.getChildren().add(circuloFinal);
        }
        if (estado.getTipo() == Estado.TipoEstado.INICIAL || estado.getTipo() == INICIAL_E_FINAL) {
            Polygon setaInicial = new Polygon(5.0, 0.0, -10.0, 7.0, -10.0, -7.0);
            setaInicial.setFill(Color.BLACK);
            Group grupoSetaInicial = new Group(setaInicial);
            grupoSetaInicial.layoutXProperty().bind(estado.xProperty().subtract(RAIO_ESTADO + 10));
            grupoSetaInicial.layoutYProperty().bind(estado.yProperty());
            g_transicoes.getChildren().add(grupoSetaInicial);
            estado.setSetaInicialVisual(grupoSetaInicial);
        }
        estado.setCirculo(circuloPrincipal);
        return stackPane;
    }

    private void configurarEventosEstado(Estado estado) {
        StackPane visualEstado = estado.getRepresentacaoVisual();
        visualEstado.setOnMouseEntered(e -> visualEstado.setCursor(Cursor.HAND));
        visualEstado.setOnMouseExited(e -> visualEstado.setCursor(Cursor.DEFAULT));
        visualEstado.setOnMousePressed(e -> {
            if (!bt_ligar.isSelected()) {
                offsetX = e.getSceneX() - estado.getX();
                offsetY = e.getSceneY() - estado.getY();
                visualEstado.setCursor(Cursor.CLOSED_HAND);
                e.consume();
            }
        });
        visualEstado.setOnMouseDragged(e -> {
            if (!bt_ligar.isSelected()) {
                double novoX = e.getSceneX() - offsetX;
                double novoY = e.getSceneY() - offsetY;
                estado.xProperty().set(novoX);
                estado.yProperty().set(novoY);
                e.consume();
            }
        });
        visualEstado.setOnMouseReleased(e -> visualEstado.setCursor(Cursor.HAND));
        visualEstado.setOnMouseClicked(e -> {
            if (bt_ligar.isSelected())
                manipularCriacaoLigacao(estado);
            e.consume();
        });

        // Início da modificação
        ContextMenu contextMenu = new ContextMenu();
        Menu tipoMenu = new Menu("Definir tipo");
        MenuItem normalItem = new MenuItem("Normal");
        normalItem.setOnAction(e -> mudarTipoEstado(estado, Estado.TipoEstado.NORMAL));

        MenuItem inicialItem = new MenuItem("Inicial");
        inicialItem.setOnAction(e -> mudarTipoEstado(estado, Estado.TipoEstado.INICIAL));

        MenuItem finalItem = new MenuItem("Final");
        finalItem.setOnAction(e -> mudarTipoEstado(estado, FINAL));

        MenuItem inicialFinalItem = new MenuItem("Inicial e Final");
        inicialFinalItem.setOnAction(e -> mudarTipoEstado(estado, INICIAL_E_FINAL));

        tipoMenu.getItems().addAll(normalItem, inicialItem, finalItem, inicialFinalItem);

        MenuItem removerItem = new MenuItem("Remover Estado");
        removerItem.setOnAction(e -> removerEstado(estado));

        contextMenu.getItems().addAll(tipoMenu, new SeparatorMenuItem(), removerItem);
        visualEstado.setOnContextMenuRequested(e ->
                contextMenu.show(visualEstado, e.getScreenX(), e.getScreenY()));
    }

    private void mudarTipoEstado(Estado estado, Estado.TipoEstado novoTipo) {
        if (estado.getTipo() != novoTipo) {
            boolean tornandoSeInicial = (novoTipo == Estado.TipoEstado.INICIAL || novoTipo == INICIAL_E_FINAL);
            boolean eraInicial = (estado.getTipo() == Estado.TipoEstado.INICIAL || estado.getTipo() == INICIAL_E_FINAL);

            if (tornandoSeInicial && estadoInicialAtual != null && estadoInicialAtual != estado) {
                Estado.TipoEstado tipoAntigo = estadoInicialAtual.getTipo() == INICIAL_E_FINAL
                        ? FINAL
                        : Estado.TipoEstado.NORMAL;
                estadoInicialAtual.setTipo(tipoAntigo);
                redesenharEstado(estadoInicialAtual);
            }
            estado.setTipo(novoTipo);
            if (tornandoSeInicial)
                estadoInicialAtual = estado;
            else if (eraInicial) // Se o estado era o inicial e deixou de ser
                estadoInicialAtual = null;

            redesenharEstado(estado);
        }
    }

    private void redesenharEstado(Estado estado) {
        StackPane visualAntigo = estado.getRepresentacaoVisual();

        g_estados.getChildren().remove(visualAntigo);
        mapaEstados.remove(visualAntigo);
        if (estado.getSetaInicialVisual() != null) {
            g_transicoes.getChildren().remove(estado.getSetaInicialVisual());
            estado.setSetaInicialVisual(null);
        }

        // Cria o novo visual
        StackPane novoVisual = criarVisualEstado(estado);
        estado.setRepresentacaoVisual(novoVisual);

        // Adiciona os novos elementos
        mapaEstados.put(novoVisual, estado);
        g_estados.getChildren().add(novoVisual);

        // Reconfigura os eventos
        configurarEventosEstado(estado);
    }

    private void manipularCriacaoLigacao(Estado estadoClicado) {
        if (estadoOrigemLigacao == null) {
            estadoOrigemLigacao = estadoClicado;
            Shape circulo = (Shape) estadoOrigemLigacao.getRepresentacaoVisual().lookup(".circulo-principal");
            if (circulo != null) circulo.setStroke(Color.RED);
        } else {
            Optional<Ligacao> ligacaoExistenteOpt = mapaTransicoes.keySet().stream()
                    .filter(lig -> lig.getOrigem() == estadoOrigemLigacao && lig.getDestino() == estadoClicado)
                    .findFirst();
            Optional<String> result = "".describeConstable();
            try {
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(TextoController.class.getResource("texto-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());

                stage.setTitle("Criar ligação");
                stage.setScene(scene);
                stage.initStyle(StageStyle.UTILITY);
                stage.setResizable(false);

                stage.showAndWait();

                String resultado = TextoController.caractere;
                result = TextoController.caractere.describeConstable();
                System.out.println(result);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            if (result.isPresent() && !result.get().trim().isEmpty()) {
                String novaCondicao = result.get().trim();
                if (ligacaoExistenteOpt.isPresent()) {
                    Ligacao ligacaoExistente = ligacaoExistenteOpt.get();
                    Transicao transicaoExistente = mapaTransicoes.get(ligacaoExistente);

                    // Pega a string de condições atual.
                    String condicaoAntiga = transicaoExistente.getCondicao();

                    // Quebra a string em partes, usando a vírgula como separador e removendo os espaços.
                    List<String> partes = Arrays.asList(condicaoAntiga.split("\\s*,\\s*"));

                    // Adiciona todas as partes a um TreeSet para ordenar e remover duplicatas.
                    Set<String> condicoesOrdenadas = new TreeSet<>(partes);

                    // Adiciona a nova condição. O TreeSet ignora se ela já existir.
                    condicoesOrdenadas.add(novaCondicao);

                    // Junta os elementos do TreeSet de volta em uma única string, formatada.
                    String condicaoAtualizada = String.join(", ", condicoesOrdenadas);
                    transicaoExistente.setCondicao(condicaoAtualizada);
                    ligacaoExistente.setCondicao(condicaoAtualizada);
                } else {
                    Transicao novaTransicao = new Transicao(estadoOrigemLigacao, estadoClicado, novaCondicao);
                    listaTransicoesModelo.add(novaTransicao);
                    Ligacao novaLigacao = new Ligacao(novaTransicao.getOrigem(), novaTransicao.getDestino(), novaTransicao.getCondicao(), 0);
                    estadoOrigemLigacao.adicionarTransicao(novaTransicao);
                    mapaTransicoes.put(novaLigacao, novaTransicao);
                    g_transicoes.getChildren().add(novaLigacao);
                    configurarEventosTransicao(novaLigacao);
                    if (estadoOrigemLigacao != estadoClicado) {
                        atualizarCurvasEntre(estadoOrigemLigacao, estadoClicado);
                    }
                }
            }
            Shape circulo = (Shape) estadoOrigemLigacao.getRepresentacaoVisual().lookup(".circulo-principal");
            if (circulo != null) circulo.setStroke(Color.BLACK);
            estadoOrigemLigacao = null;
            tg_acao.selectToggle(null);
        }
    }

    private void configurarEventosTransicao(Ligacao ligacao) {
        ligacao.setOnMouseEntered(e -> ligacao.setCursor(Cursor.HAND));
        ligacao.setOnMouseExited(e -> ligacao.setCursor(Cursor.DEFAULT));

        ContextMenu contextMenu = new ContextMenu();
        MenuItem editarItem = new MenuItem("Editar Condição");
        editarItem.setOnAction(e -> editarTransicao(ligacao));
        MenuItem removerItem = new MenuItem("Remover Ligação");
        removerItem.setOnAction(e -> removerTransicao(ligacao));

        contextMenu.getItems().addAll(editarItem, removerItem);

        ligacao.setOnContextMenuRequested(e ->
                contextMenu.show(ligacao, e.getScreenX(), e.getScreenY()));
    }

    private void removerEstado(Estado estadoParaRemover) {
        if (estadoParaRemover == estadoInicialAtual)
            estadoInicialAtual = null;
        List<Ligacao> setasParaRemover = new ArrayList<>();
        Iterator<Map.Entry<Ligacao, Transicao>> iterator = mapaTransicoes.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Ligacao, Transicao> entry = iterator.next();
            Transicao transicao = entry.getValue();
            if (transicao.getOrigem() == estadoParaRemover || transicao.getDestino() == estadoParaRemover) {
                if (transicao.getDestino() == estadoParaRemover)
                    transicao.getOrigem().removerTransicao(transicao);
                setasParaRemover.add(entry.getKey());
                listaTransicoesModelo.remove(transicao);
                iterator.remove();
            }
        }
        g_transicoes.getChildren().removeAll(setasParaRemover);
        if (estadoParaRemover.getSetaInicialVisual() != null) {
            g_transicoes.getChildren().remove(estadoParaRemover.getSetaInicialVisual());
        }
        StackPane visualEstado = estadoParaRemover.getRepresentacaoVisual();
        g_estados.getChildren().remove(visualEstado);
        mapaEstados.remove(visualEstado);
        idsEmUso.remove(estadoParaRemover.getId());
    }

    private void removerTransicao(Ligacao ligacaoParaRemover) {
        Transicao transicao = mapaTransicoes.get(ligacaoParaRemover);
        if (transicao != null) {
            Estado origem = transicao.getOrigem();
            Estado destino = transicao.getDestino();

            origem.removerTransicao(transicao);

            g_transicoes.getChildren().remove(ligacaoParaRemover);
            mapaTransicoes.remove(ligacaoParaRemover);
            listaTransicoesModelo.remove(transicao);

            // Reajusta as curvas das setas restantes
            if (origem != destino)
                atualizarCurvasEntre(origem, destino);
        }
    }

    private void editarTransicao(Ligacao ligacao) {
        Transicao transicao = mapaTransicoes.get(ligacao);
        if (transicao != null) {
            Optional<String> result = "".describeConstable();
            try {
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(TextoController.class.getResource("texto-view.fxml"));
                Scene scene = new Scene(fxmlLoader.load());

                // Obtém a instância do controller que acabou de ser criada e chama o método.
                TextoController textoController = fxmlLoader.getController();
                textoController.setTextoInicial(transicao.getCondicao());

                stage.setTitle("Editar condição");
                stage.setScene(scene);
                stage.initStyle(StageStyle.UTILITY);
                stage.setResizable(false);
                stage.initOwner(painel_automato.getScene().getWindow());

                TextoController.caractere = null;
                stage.showAndWait();

                // Pega o resultado da variável estática após o fechamento da janela
                result = TextoController.caractere.describeConstable();
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            result.ifPresent(novaCondicao -> {
                if (!novaCondicao.trim().isEmpty()) {
                    String condicaoFinal = novaCondicao.trim();
                    transicao.setCondicao(condicaoFinal);
                    ligacao.setCondicao(condicaoFinal);
                }
            });
        }
    }

    private void atualizarCurvasEntre(Estado estado1, Estado estado2) {
        // Separa as ligações em duas listas, uma para cada direção.
        List<Ligacao> ligacoes1para2 = mapaTransicoes.keySet().stream()
                .filter(lig -> lig.getOrigem() == estado1 && lig.getDestino() == estado2)
                .collect(Collectors.toList());
        List<Ligacao> ligacoes2para1 = mapaTransicoes.keySet().stream()
                .filter(lig -> lig.getOrigem() == estado2 && lig.getDestino() == estado1)
                .collect(Collectors.toList());
        aplicarCurvaturas(ligacoes1para2);
        aplicarCurvaturas(ligacoes2para1);
        // A geometria do desenho fará com que elas se curvem para o lado oposto automaticamente.
    }

    private void aplicarCurvaturas(List<Ligacao> ligacoes) {
        if (!ligacoes.isEmpty()) {
            if (ligacoes.size() == 1) { // Se houver apenas uma ligação neste grupo, ela pode ser reta.
                // Verifica se há alguma seta na direção oposta.
                Ligacao unicaLigacao = ligacoes.getFirst();
                boolean temSetaOposta = mapaTransicoes.keySet().stream()
                        .anyMatch(l -> l.getOrigem() == unicaLigacao.getDestino() && l.getDestino() == unicaLigacao.getOrigem());
                if (temSetaOposta)
                    unicaLigacao.setCurvatura(30.0);
                else // Senão, pode ser reta.
                    unicaLigacao.setCurvatura(0);
            } else { // Se houver múltiplas ligações na mesma direção, aplica curvaturas alternadas.
                double curvaturaBase = 30.0;
                for (int i = 0; i < ligacoes.size(); i++) {
                    double magnitude = Math.ceil((i + 1.0) / 2.0) * curvaturaBase;
                    double sinal = (i % 2 == 0) ? 1.0 : -1.0;
                    ligacoes.get(i).setCurvatura(magnitude * sinal);
                }
            }
        }
    }

    private Circle recuperarCirculoDoEstado(Estado estado) {
        if (estado != null && estado.getRepresentacaoVisual() != null) {
            Node noDoCirculo = estado.getRepresentacaoVisual().lookup(".circulo-principal");
            if (noDoCirculo instanceof Circle)
                return (Circle) noDoCirculo;
        }
        return null;
    }

    public ArrayList<Estado> saida(ArrayList<Transicao> transicoes, char caractere) {
        ArrayList<Estado> destinos = new ArrayList<>();
        int i = 0;

        for (Transicao transicao : transicoes) {
            if (transicao.getCondicao().contains(String.valueOf(caractere)))
                destinos.add(transicao.getDestino());
        }
        return destinos;
    }

    public void onPassoPasso(ActionEvent actionEvent) {
        if (estadoInicialAtual != null) {
            bt_manual.setDisable(true);
            bt_automatico.setDisable(true);
            bt_proximo.setDisable(false);
            lb_resultado.setText("");

            mapaEstados.values().forEach(estado -> {
                Circle c = recuperarCirculoDoEstado(estado);
                if (c != null) {
                    c.setFill(Color.LIGHTYELLOW);
                }
            });

            i_manual = 0;
            atuais_manual = new ArrayList<>();
            atuais_manual.add(estadoInicialAtual);
            estadoInicialAtual.getCirculo().setFill(Color.ORANGE);
            palavra = tf_palavra.getText();
        }
    }

    public void onProximoPasso(ActionEvent actionEvent) {
        ArrayList<Transicao> transicoes;
        int tam;
        if(!atuais_manual.isEmpty() && i_manual<palavra.length()) {
            tam = atuais_manual.size();
            for (int j = 0; j < tam; j++) {
                transicoes = atuais_manual.get(j).getTransicoes();
                ArrayList<Estado> destinos = saida(transicoes, palavra.charAt(i_manual));
                atuais_manual.addAll(destinos);
            }
            mapaEstados.values().forEach(estado -> {
                Circle c = recuperarCirculoDoEstado(estado);
                if (c != null) {
                    c.setFill(Color.LIGHTYELLOW);
                }
            });
            for (int j = 0; j < tam; j++) {
                atuais_manual.getFirst().getCirculo().setFill(Color.LIGHTYELLOW);
                atuais_manual.removeFirst();
            }
            for (Estado estado : atuais_manual)
                estado.getCirculo().setFill(Color.ORANGE);
            i_manual++;
        }
        if (i_manual == palavra.length()) {
            bt_proximo.setDisable(true);
            bt_manual.setDisable(false);
            bt_automatico.setDisable(false);
            if(palavra.isEmpty())
                palavra ="ε";
            int i = 0;
            while (i < atuais_manual.size()&&(atuais_manual.get(i).getTipo() !=FINAL &&atuais_manual.get(i).getTipo() !=INICIAL_E_FINAL))
                i++;
            if (i < atuais_manual.size()) {
                lb_resultado.setText("Palavra '" + palavra + "' aceita");
                for (Estado s : atuais_manual)
                    if (s.getTipo() == FINAL || s.getTipo() == INICIAL_E_FINAL)
                        recuperarCirculoDoEstado(s).setFill(Color.LIGHTGREEN);
                    else
                        recuperarCirculoDoEstado(s).setFill(Color.LIGHTCORAL);
            } else {
                lb_resultado.setText("Palavra '" + palavra + "' não aceita");
                for (Estado s : atuais_manual)
                    recuperarCirculoDoEstado(s).setFill(Color.LIGHTCORAL);
            }
        }
    }

    public void onAutomatico(ActionEvent actionEvent) {
        if (estadoInicialAtual != null) {
            AtomicReference<String> palavra = new AtomicReference<>(tf_palavra.getText());
            lb_resultado.setText("");

            mapaEstados.values().forEach(estado -> {
                Circle c = recuperarCirculoDoEstado(estado);
                if (c != null) {
                    c.setFill(Color.LIGHTYELLOW);
                }
            });

            SequentialTransition animacaoSequencial = new SequentialTransition();
            AtomicReference<ArrayList<Estado>> atuaisRef = new AtomicReference<>(new ArrayList<>());
            atuaisRef.get().add(estadoInicialAtual);

            Circle circuloInicial = recuperarCirculoDoEstado(estadoInicialAtual);
            if (circuloInicial != null)
                circuloInicial.setFill(Color.ORANGE);

            for (char caractere : palavra.get().toCharArray()) {
                PauseTransition passo = new PauseTransition(Duration.millis(800));
                passo.setOnFinished(e -> {
                    ArrayList<Estado> estadosAtuais = atuaisRef.get();
                    if (!estadosAtuais.isEmpty()) {
                        Set<Estado> proximoSet = new HashSet<>();
                        for (Estado umEstadoAtual : estadosAtuais) {
                            ArrayList<Estado> destinos = saida(umEstadoAtual.getTransicoes(), caractere);
                            proximoSet.addAll(destinos);
                        }
                        ArrayList<Estado> proximosEstados = new ArrayList<>(proximoSet);
                        for (Estado anterior : estadosAtuais) {
                            Circle c = recuperarCirculoDoEstado(anterior);
                            if (c != null)
                                c.setFill(Color.LIGHTYELLOW);
                        }
                        for (Estado proximo : proximosEstados) {
                            Circle c = recuperarCirculoDoEstado(proximo);
                            if (c != null)
                                c.setFill(Color.ORANGE);
                        }
                        atuaisRef.set(proximosEstados);
                    }
                });
                animacaoSequencial.getChildren().add(passo);
            }

            animacaoSequencial.setOnFinished(e -> {
                ArrayList<Estado> estadosFinais = atuaisRef.get();
                boolean aceita = false;
                for(Estado finalEstado : estadosFinais)
                    if (finalEstado.getTipo() == FINAL || finalEstado.getTipo() == INICIAL_E_FINAL) {
                        aceita = true;
                        break;
                    }
                if (palavra.get().isEmpty())
                    palavra.set("ε");
                if (aceita) {
                    lb_resultado.setText("Palavra '" + palavra + "' aceita");
                    for(Estado s : estadosFinais)
                        if (s.getTipo() == FINAL || s.getTipo() == INICIAL_E_FINAL)
                            recuperarCirculoDoEstado(s).setFill(Color.LIGHTGREEN);
                        else
                            recuperarCirculoDoEstado(s).setFill(Color.LIGHTCORAL);
                } else {
                    lb_resultado.setText("Palavra '" + palavra + "' não aceita");
                    for (Estado s : estadosFinais)
                        recuperarCirculoDoEstado(s).setFill(Color.LIGHTCORAL);
                }
            });
            animacaoSequencial.play();
        }
    }

    public void onLimpar(ActionEvent actionEvent) {
        estadoInicialAtual = null;
        estadoOrigemLigacao = null;

        bt_manual.setDisable(false);
        bt_automatico.setDisable(false);
        bt_proximo.setDisable(true);

        g_estados.getChildren().clear();
        g_transicoes.getChildren().clear();
        mapaEstados.clear();
        mapaTransicoes.clear();
        listaTransicoesModelo.clear();
        idsEmUso.clear();
        lb_resultado.setText("");
        tf_palavra.clear();
    }
}