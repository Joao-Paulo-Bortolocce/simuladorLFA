package unoeste.termo6.simulador.automato;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Group;
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
import unoeste.termo6.simulador.MainController;

import java.util.*;
import java.util.stream.Collectors;

public class AutomatoController {
    private static final double RAIO_ESTADO = 25;

    public ToggleButton bt_estado;
    public ToggleButton bt_inicial;
    public ToggleButton bt_inicial_final;
    public ToggleButton bt_final;
    public ToggleButton bt_ligar;

    public Pane painel_automato;

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
    private String botao_verde_padrao;
    private String botao_azul_selecionado;
    private String botao_verde_selecionado;
    private String botao_azul_hover;
    private String botao_verde_hover;

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

        // Define os estilos padrão e selecionados (com borda) aqui no initialize
        // para os botões azuis
        botao_azul_padrao = "-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                "-fx-border-color: transparent; -fx-border-width: 2px; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);";
        botao_azul_selecionado = "-fx-background-color: #2980b9; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                "-fx-border-color: #f1c40f; -fx-border-width: 2px; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(241, 196, 15, 0.6), 10, 0, 0, 0);";
        botao_azul_hover = "-fx-background-color: #4fb8ff; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                "-fx-border-color: transparent; -fx-border-width: 2px; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);";

        // para o botão verde (btLigar)
        botao_verde_padrao = "-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                "-fx-border-color: transparent; -fx-border-width: 2px; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);";
        botao_verde_selecionado = "-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                "-fx-border-color: #f1c40f; -fx-border-width: 2px; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(241, 196, 15, 0.6), 10, 0, 0, 0);";
        botao_verde_hover = "-fx-background-color: #3ddc83; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 8; -fx-padding: 8 15; -fx-cursor: hand; " +
                "-fx-border-color: transparent; -fx-border-width: 2px; -fx-border-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 1);";


        // Listener para o grupo de tipo de estado (botões azuis)
        tg_tipo_estado.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (oldToggle != null) {
                // Restaura o estilo do botão que foi deselecionado
                ToggleButton oldBtn = (ToggleButton) oldToggle;
                oldBtn.setStyle(botao_azul_padrao);
            }
            if (newToggle != null) {
                // Aplica o estilo de seleção ao novo botão
                ToggleButton newBtn = (ToggleButton) newToggle;
                newBtn.setStyle(botao_azul_selecionado);
                tg_acao.selectToggle(null); // Deseleciona o outro grupo
            } else {
                // Se nenhum botão estiver selecionado (ex: por deseleção programática),
                // garanta que todos voltem ao estado normal
                bt_estado.setStyle(botao_azul_padrao);
                bt_inicial.setStyle(botao_azul_padrao);
                bt_inicial_final.setStyle(botao_azul_padrao);
                bt_final.setStyle(botao_azul_padrao);
            }
        });

        // Listener para o grupo de ações (botão verde)
        tg_acao.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (oldToggle != null) {
                // Restaura o estilo do botão que foi deselecionado (btLigar)
                ToggleButton oldBtn = (ToggleButton) oldToggle;
                if (oldBtn == bt_ligar) { // Apenas btLigar está neste grupo, mas é bom verificar
                    oldBtn.setStyle(botao_verde_padrao);
                }
            }
            if (newToggle != null) {
                // Aplica o estilo de seleção ao novo botão (btLigar)
                ToggleButton newBtn = (ToggleButton) newToggle;
                if (newBtn == bt_ligar) {
                    newBtn.setStyle(botao_verde_selecionado);
                }
                tg_tipo_estado.selectToggle(null); // Deseleciona o outro grupo
            } else {
                // Se nenhum botão estiver selecionado (ex: por deseleção programática)
                bt_ligar.setStyle(botao_verde_padrao);
            }
        });

        // Garante que o btEstado já comece selecionado com o estilo correto
        bt_estado.setSelected(true);
        bt_estado.setStyle(botao_azul_selecionado); // Aplica o estilo de seleção inicial
    }

    // Métodos de hover (onMouseEntered e onMouseExited)
    @FXML
    private void onToggleButtonHover(MouseEvent event) {
        ToggleButton button = (ToggleButton) event.getSource();
        if (!button.isSelected()) { // Só aplica o hover se o botão não estiver selecionado
            if (button == bt_ligar) {
                button.setStyle(botao_verde_hover);
            } else {
                button.setStyle(botao_azul_hover);
            }
        }
    }

    @FXML
    private void onToggleButtonExit(MouseEvent event) {
        ToggleButton button = (ToggleButton) event.getSource();
        if (!button.isSelected()) { // Só reverte o estilo se o botão não estiver selecionado
            if (button == bt_ligar) {
                button.setStyle(botao_verde_padrao);
            } else {
                button.setStyle(botao_azul_padrao);
            }
        }
    }

    @FXML
    void manipularClickPainel(MouseEvent event) {
        if (event.getTarget() == painel_automato) {
            ToggleButton tipoSelecionado = (ToggleButton) tg_tipo_estado.getSelectedToggle();
            if (tipoSelecionado != null) {
                Estado.TipoEstado tipo = Estado.TipoEstado.NORMAL;
                if (tipoSelecionado == bt_inicial) tipo = Estado.TipoEstado.INICIAL;
                else if (tipoSelecionado == bt_final) tipo = Estado.TipoEstado.FINAL;
                else if (tipoSelecionado == bt_inicial_final) { // <-- ADICIONE ESTE ELSE IF
                    tipo = Estado.TipoEstado.INICIAL_E_FINAL;
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
        if (tipo == Estado.TipoEstado.INICIAL || tipo == Estado.TipoEstado.INICIAL_E_FINAL)
            // Se já existe um estado inicial, precisamos "rebaixá-lo"
            if (estadoInicialAtual != null) {
                // AQUI ESTÁ A LÓGICA: Se o antigo era INICIAL_E_FINAL, vira FINAL. Senão, vira NORMAL.
                Estado.TipoEstado novoTipoParaAntigoInicial = estadoInicialAtual.getTipo() == Estado.TipoEstado.INICIAL_E_FINAL
                        ? Estado.TipoEstado.FINAL
                        : Estado.TipoEstado.NORMAL;
                // Usa o método mudarTipoEstado para garantir que a interface seja atualizada corretamente
                mudarTipoEstado(estadoInicialAtual, novoTipoParaAntigoInicial);
            }

        int novoId = gerarProximoId();
        idsEmUso.add(novoId);
        Estado novoEstado = new Estado(novoId, x, y, tipo);

        // Atualiza a referência para o novo estado inicial
        if (novoEstado.getTipo() == Estado.TipoEstado.INICIAL || novoEstado.getTipo() == Estado.TipoEstado.INICIAL_E_FINAL)
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

        if (estado.getTipo() == Estado.TipoEstado.FINAL || estado.getTipo() == Estado.TipoEstado.INICIAL_E_FINAL) {
            Circle circuloFinal = new Circle(RAIO_ESTADO - 5, Color.TRANSPARENT);
            circuloFinal.setStroke(Color.BLACK);
            circuloFinal.setStrokeWidth(1.5);
            stackPane.getChildren().add(circuloFinal);
        }
        if (estado.getTipo() == Estado.TipoEstado.INICIAL || estado.getTipo() == Estado.TipoEstado.INICIAL_E_FINAL) {
            Polygon setaInicial = new Polygon(5.0, 0.0, -10.0, 7.0, -10.0, -7.0);
            setaInicial.setFill(Color.BLACK);
            Group grupoSetaInicial = new Group(setaInicial);
            grupoSetaInicial.layoutXProperty().bind(estado.xProperty().subtract(RAIO_ESTADO + 10));
            grupoSetaInicial.layoutYProperty().bind(estado.yProperty());
            g_transicoes.getChildren().add(grupoSetaInicial);
            estado.setSetaInicialVisual(grupoSetaInicial);
        }
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
        finalItem.setOnAction(e -> mudarTipoEstado(estado, Estado.TipoEstado.FINAL));

        MenuItem inicialFinalItem = new MenuItem("Inicial e Final");
        inicialFinalItem.setOnAction(e -> mudarTipoEstado(estado, Estado.TipoEstado.INICIAL_E_FINAL));

        tipoMenu.getItems().addAll(normalItem, inicialItem, finalItem, inicialFinalItem);

        MenuItem removerItem = new MenuItem("Remover Estado");
        removerItem.setOnAction(e -> removerEstado(estado));

        contextMenu.getItems().addAll(tipoMenu, new SeparatorMenuItem(), removerItem);
        visualEstado.setOnContextMenuRequested(e ->
                contextMenu.show(visualEstado, e.getScreenX(), e.getScreenY()));
    }

    private void mudarTipoEstado(Estado estado, Estado.TipoEstado novoTipo) {
        if (estado.getTipo() != novoTipo) {
            boolean tornandoSeInicial = (novoTipo == Estado.TipoEstado.INICIAL || novoTipo == Estado.TipoEstado.INICIAL_E_FINAL);
            boolean eraInicial = (estado.getTipo() == Estado.TipoEstado.INICIAL || estado.getTipo() == Estado.TipoEstado.INICIAL_E_FINAL);

            if (tornandoSeInicial && estadoInicialAtual != null && estadoInicialAtual != estado) {
                Estado.TipoEstado tipoAntigo = estadoInicialAtual.getTipo() == Estado.TipoEstado.INICIAL_E_FINAL
                        ? Estado.TipoEstado.FINAL
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
                FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("texto-view.fxml"));
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
        while (iterator.hasNext()){
            Map.Entry<Ligacao, Transicao> entry = iterator.next();
            Transicao transicao = entry.getValue();
            if (transicao.getOrigem() == estadoParaRemover || transicao.getDestino() == estadoParaRemover) {
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
                FXMLLoader fxmlLoader = new FXMLLoader(MainController.class.getResource("texto-view.fxml"));
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
}