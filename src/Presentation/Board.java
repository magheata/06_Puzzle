/**
 * AUTHORS: RAFAEL ADRIÁN GIL CAÑESTRO
 *          MIRUNA ANDREEA GHEATA
 */
package Presentation;

import Application.Controller;
import Config.Constants;
import Domain.Position;
import Domain.Tile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.*;

public class Board extends JPanel {
    private Figure[][] figuresBoard;
    private JPanel[][] panels;
    private Map<JPanel, Figure> figureForPanel = new HashMap<>();
    private Map<Integer, Figure> figureForValue = new HashMap<>();
    private int size;
    private Controller controller;

    public Board(Controller controller){
        this.controller = controller;
        this.controller.setBoardView(this);
        size = this.controller.getSize();
        figuresBoard = new Figure[size][size];
        panels = new JPanel[size][size];
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                panels[i][j] = new JPanel();
                panels[i][j].setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
                panels[i][j].setBorder(new EmptyBorder(0, 0, 0, 0));
            }
        }
        this.setLayout(new GridBagLayout());
        this.setBorder(new EmptyBorder(0, 0, 0, 0));
        this.setSize(Constants.DIM_BOARD);
        this.setMinimumSize(Constants.DIM_BOARD);
        this.setPreferredSize(Constants.DIM_BOARD);
    }

    public void initTiles(ArrayList<Tile> tiles){
        int x, y;
        int figureWidth = this.getWidth() / size;
        int figureHeight = this.getHeight() / size;
        for (int i = 0; i < tiles.size(); i++){
            Tile tile = tiles.get(i);
            x = tile.getPosition().getX() % size;
            y = tile.getPosition().getY() % size;
            if (tile.getImage() != null){
                figuresBoard[x][y] = new Figure(controller, x * figureWidth, y * figureHeight, tile.getImage(), tile.getGoalValue(), figureWidth);
            } else {
                figuresBoard[x][y] = new Figure(controller, x * figureWidth, y * figureHeight, tile.getGoalValue(), figureWidth);
            }
            figureForValue.put(tile.getGoalValue(), figuresBoard[x][y]);
            panels[x][y].add(figuresBoard[x][y]);
            figureForPanel.put(panels[x][y], figuresBoard[x][y]);
        }
        addTilesToBoard();
    }

    public void repaintFigures(ArrayList<Tile> tiles) {
        Iterator it = tiles.iterator();
        while (it.hasNext()){
            Tile tile = (Tile) it.next();
            int xPos = tile.getPosition().getX();
            int yPos = tile.getPosition().getY();
            Figure fig = figureForPanel.get(panels[xPos][yPos]);
            if (tile.getGoalValue() != 0){
                if (tile.getImage() == null){
                    fig.setText(String.valueOf(tile.getGoalValue()));
                    fig.setOpaque(true);
                    fig.setBackground(Constants.BG_COLOR);
                    fig.setForeground(Constants.FG_COLOR);
                } else {
                    fig.setImage(tile.getImage());
                }
            } else {
                fig.setImage(null);
                fig.setText("");
                fig.setOpaque(false);
                fig.setBackground(Constants.FG_COLOR);
                fig.setForeground(Constants.FG_COLOR);
            }
        }

        this.revalidate();
        this.repaint();
    }

    public void updateTilePos(Position[] positions) {

        Position blankTilePos = positions[0];
        Position swappedTilePos = positions[1];

        Figure blankFigure = figuresBoard[blankTilePos.getX()][blankTilePos.getY()];
        Figure swappedFigure = figuresBoard[swappedTilePos.getX()][swappedTilePos.getY()];

        int xAux = blankFigure.getxPos();
        int yAux = blankFigure.getyPos();

        blankFigure.setxPos(swappedFigure.getxPos());
        blankFigure.setyPos(swappedFigure.getyPos());

        swappedFigure.setxPos(xAux);
        swappedFigure.setyPos(yAux);

        figuresBoard[swappedTilePos.getX()][swappedTilePos.getY()] = blankFigure;
        figuresBoard[blankTilePos.getX()][blankTilePos.getY()] = swappedFigure;

        panels[swappedTilePos.getX()][swappedTilePos.getY()] = getPanelForFigure(blankFigure);
        panels[blankTilePos.getX()][blankTilePos.getY()] = getPanelForFigure(swappedFigure);
    }

    private JPanel getPanelForFigure(Figure fig){
        for (Map.Entry<JPanel, Figure> entry : figureForPanel.entrySet()) {
            if (fig == (entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void resetBoard(){
        figureForPanel.clear();
        figureForValue.clear();
        for (int i = 0; i < panels[0].length; i++){
            for (int j = 0; j< panels[0].length; j++){
                panels[i][j].removeAll();
            }
        }
        this.removeAll();
        this.revalidate();
    }

    public void enableFigureButtons(boolean enabled){
        Object[] figures = figureForPanel.entrySet().toArray();
        for (int i = 0; i < figures.length; i++){
            Map.Entry<JPanel, Figure> figure = (Map.Entry<JPanel, Figure>) figures[i];
            figure.getValue().setEnabled(enabled);
            figure.getValue().setForeground(Constants.FG_COLOR);
        }
    }

    public void updateFiguresInPanels(ArrayList<Tile> tiles){
        int x, y;
        for (int i = 0; i < tiles.size(); i++){
            Tile tile = tiles.get(i);

            x = tile.getPosition().getX() % size;
            y = tile.getPosition().getY() % size;

            Figure figure = figureForValue.get(tile.getGoalValue());

            figure.setxPos(x * Constants.TILE_SIZE);
            figure.setyPos(y * Constants.TILE_SIZE);

            figuresBoard[x][y] = figure;

            figureForPanel.remove(panels[x][y]);
            panels[x][y].removeAll();

            panels[x][y].add(figuresBoard[x][y]);
            figureForPanel.put(panels[x][y], figuresBoard[x][y]);
        }
        addTilesToBoard();
    }

    private void addTilesToBoard(){
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < size; i++){
                for (int j = 0; j < size; j++){
                    GridBagConstraints constraints = new GridBagConstraints();
                    constraints.gridx = i;
                    constraints.gridy = j;
                    this.add(panels[i][j], constraints);

                }
            }
            this.repaint();
            this.revalidate();
        });
    }
}
