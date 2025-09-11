package unoeste.termo6.simulador.automato;

import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextBoundsType;
import javafx.scene.transform.Rotate;

public class Ligacao extends Group {
    private final Estado origem;
    private final Estado destino;
    private double curvatura;

    private final Path linha = new Path();
    private final Polygon cabecaSeta = new Polygon();
    private final Text texto = new Text();

    private static final double RAIO_ESTADO = 25;
    private static final double TAMANHO_PONTA_SETA = 10.0;

    public Ligacao(Estado origem, Estado destino, String condicao, double curvatura) {
        this.origem = origem;
        this.destino = destino;
        this.curvatura = curvatura;

        origem.xProperty().addListener(observable -> atualizarPosicao());
        origem.yProperty().addListener(observable -> atualizarPosicao());
        destino.xProperty().addListener(observable -> atualizarPosicao());
        destino.yProperty().addListener(observable -> atualizarPosicao());

        linha.setStroke(Color.BLACK);
        linha.setStrokeWidth(1.5);
        linha.setFill(Color.TRANSPARENT);

        cabecaSeta.getPoints().addAll(0.0, 0.0, -TAMANHO_PONTA_SETA, 5.0, -TAMANHO_PONTA_SETA, -5.0);
        cabecaSeta.setFill(Color.BLACK);

        texto.setText(condicao);
        texto.setBoundsType(TextBoundsType.VISUAL);
        texto.setStyle("-fx-font-weight: bold;");

        this.getChildren().addAll(linha, cabecaSeta, texto);
        atualizarPosicao();
    }

    public Estado getOrigem() {
        return origem;
    }

    public Estado getDestino() {
        return destino;
    }

    public void setCurvatura(double offset) {
        this.curvatura = offset;
        atualizarPosicao();
    }

    public void setCondicao(String condicao) {
        this.texto.setText(condicao);
        atualizarPosicao();
    }

    public void atualizarPosicao() {
        if (origem == destino) {
            desenharLoop();
        } else {
            desenharSetaRetaOuCurva();
        }
    }

    private void desenharSetaRetaOuCurva() {
        Point2D startPoint = calcularPontoBorda(origem.getX(), origem.getY(), destino.getX(), destino.getY());
        Point2D tipPosition = calcularPontoBorda(destino.getX(), destino.getY(), origem.getX(), origem.getY());

        Point2D midPoint = startPoint.midpoint(tipPosition);
        double dx = tipPosition.getX() - startPoint.getX();
        double dy = tipPosition.getY() - startPoint.getY();

        Point2D normal = (Math.abs(dx) < 1e-6 && Math.abs(dy) < 1e-6)
                ? new Point2D(0, -1)
                : new Point2D(-dy, dx).normalize();

        Point2D controlPoint = new Point2D(
                midPoint.getX() + normal.getX() * this.curvatura,
                midPoint.getY() + normal.getY() * this.curvatura
        );

        MoveTo moveTo = new MoveTo(startPoint.getX(), startPoint.getY());
        QuadCurveTo quadTo = new QuadCurveTo(controlPoint.getX(), controlPoint.getY(), tipPosition.getX(), tipPosition.getY());
        linha.getElements().clear();
        linha.getElements().addAll(moveTo, quadTo);

        Point2D tangentDirection = getTangentQuad(startPoint, controlPoint, tipPosition).normalize();
        double anguloRad = Math.atan2(tangentDirection.getY(), tangentDirection.getX());

        cabecaSeta.setTranslateX(tipPosition.getX());
        cabecaSeta.setTranslateY(tipPosition.getY());
        cabecaSeta.getTransforms().clear();
        cabecaSeta.getTransforms().add(new Rotate(Math.toDegrees(anguloRad), 0, 0));

        // ### CÓDIGO CORRIGIDO PARA POSICIONAR O TEXTO ###

        // 1. Encontra o ponto exato no meio da curva (t=0.5)
        Point2D pontoMedioCurva = getPointOnQuadCurve(startPoint, controlPoint, tipPosition);

        // 2. Calcula o vetor tangente à curva nesse ponto médio.
        // Para uma curva quadrática, a tangente no meio é simplesmente a direção da linha que une o início e o fim.
        Point2D tangenteNaCurva = tipPosition.subtract(startPoint);

        // 3. Calcula o vetor normal (perpendicular) à tangente. É para esta direção que vamos mover o texto.
        Point2D normalDaCurva = new Point2D(-tangenteNaCurva.getY(), tangenteNaCurva.getX()).normalize();

        // 4. Garante que o texto seja empurrado para o lado "de fora" da curva.
        // A direção da curvatura é dada pelo sinal de `this.curvatura`.
        // O produto escalar (dot product) nos diz se a normal que calculamos está na mesma direção da normal original.
        // Se não estiver, invertemos para garantir consistência.
        if (normalDaCurva.dotProduct(normal) < 0) {
            normalDaCurva = normalDaCurva.multiply(-1);
        }

        // 5. Define a distância do texto em relação à linha
        double textOffset = 15.0;

        // 6. Calcula a posição final do texto
        double textoX = pontoMedioCurva.getX() + normalDaCurva.getX() * textOffset;
        double textoY = pontoMedioCurva.getY() + normalDaCurva.getY() * textOffset;

        // 7. Centraliza e posiciona o texto
        texto.setX(textoX - texto.getLayoutBounds().getWidth() / 2);
        texto.setY(textoY - texto.getLayoutBounds().getHeight() / 2);
    }

    private void desenharLoop() {
        double x = origem.getX();
        double y = origem.getY();
        double raioLoop = 20.0;
        Point2D startPoint = new Point2D(x - RAIO_ESTADO * 0.5, y - RAIO_ESTADO * 0.8);
        Point2D tipPosition = new Point2D(x + RAIO_ESTADO * 0.5, y - RAIO_ESTADO * 1.2);
        Point2D control1 = new Point2D(x - raioLoop * 2.5, y - raioLoop * 3.5);
        Point2D control2 = new Point2D(x + raioLoop * 2.5, y - raioLoop * 3.5);
        MoveTo moveTo = new MoveTo(startPoint.getX(), startPoint.getY());
        CubicCurveTo cubicTo = new CubicCurveTo(control1.getX(), control1.getY(), control2.getX(), control2.getY(), tipPosition.getX(), tipPosition.getY());
        linha.getElements().clear();
        linha.getElements().addAll(moveTo, cubicTo);
        Point2D tangentDirection = getTangentCubic(startPoint, control1, control2, tipPosition).normalize();
        double anguloRad = Math.atan2(tangentDirection.getY(), tangentDirection.getX());
        cabecaSeta.setTranslateX(tipPosition.getX());
        cabecaSeta.setTranslateY(tipPosition.getY());
        cabecaSeta.getTransforms().clear();
        cabecaSeta.getTransforms().add(new Rotate(Math.toDegrees(anguloRad), 0, 0));
        Point2D pontoTopoLoop = getPointOnCubicCurve(startPoint, control1, control2, tipPosition);
        texto.setX(pontoTopoLoop.getX() - texto.getLayoutBounds().getWidth() / 2);
        texto.setY(pontoTopoLoop.getY() - 15);
    }

    private Point2D calcularPontoBorda(double cx1, double cy1, double cx2, double cy2) {
        double angle = Math.atan2(cy2 - cy1, cx2 - cx1);
        double x = cx1 + Ligacao.RAIO_ESTADO * Math.cos(angle);
        double y = cy1 + Ligacao.RAIO_ESTADO * Math.sin(angle);
        return new Point2D(x, y);
    }

    private Point2D getPointOnQuadCurve(Point2D p0, Point2D p1, Point2D p2) {
        double x = Math.pow(1 - 0.5, 2) * p0.getX() + 2 * (1 - 0.5) * 0.5 * p1.getX() + Math.pow(0.5, 2) * p2.getX();
        double y = Math.pow(1 - 0.5, 2) * p0.getY() + 2 * (1 - 0.5) * 0.5 * p1.getY() + Math.pow(0.5, 2) * p2.getY();
        return new Point2D(x, y);
    }

    private Point2D getTangentQuad(Point2D p0, Point2D p1, Point2D p2) {
        double x = 2 * (1 - 1.0) * (p1.getX() - p0.getX()) + 2 * 1.0 * (p2.getX() - p1.getX());
        double y = 2 * (1 - 1.0) * (p1.getY() - p0.getY()) + 2 * 1.0 * (p2.getY() - p1.getY());
        return new Point2D(x, y);
    }

    private Point2D getTangentCubic(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
        double x = 3 * Math.pow(1- 1.0, 2) * (p1.getX()-p0.getX()) + 6*(1- 1.0)* 1.0 *(p2.getX()-p1.getX()) + 3* 1.0 * 1.0 *(p3.getX()-p2.getX());
        double y = 3 * Math.pow(1- 1.0, 2) * (p1.getY()-p0.getY()) + 6*(1- 1.0)* 1.0 *(p2.getY()-p1.getY()) + 3* 1.0 * 1.0 *(p3.getY()-p2.getY());
        return new Point2D(x, y);
    }

    private Point2D getPointOnCubicCurve(Point2D p0, Point2D p1, Point2D p2, Point2D p3) {
        double u = 1 - 0.5;
        double tt = 0.5 * 0.5;
        double uu = u * u;
        double uuu = uu * u;
        double ttt = tt * 0.5;
        double x = uuu * p0.getX() + 3 * uu * 0.5 * p1.getX() + 3 * u * tt * p2.getX() + ttt * p3.getX();
        double y = uuu * p0.getY() + 3 * uu * 0.5 * p1.getY() + 3 * u * tt * p2.getY() + ttt * p3.getY();
        return new Point2D(x, y);
    }
}