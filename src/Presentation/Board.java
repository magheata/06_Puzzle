/* Created by andreea on 01/06/2020 */
package Presentation;

import Application.Controller;
import Config.Constants;
import Domain.Position;
import Domain.Tile;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Board extends JPanel {
    private Figure[][] tilesBoard;
    private JPanel[][] panels;
    private Map<JPanel, Figure> figureForPanel = new HashMap<>();
    private Figure tileToMove;
    private int size;
    private Controller controller;

    public Board(Controller controller){
        this.controller = controller;
        this.controller.setBoardView(this);
        size = this.controller.getSize();
        tilesBoard = new Figure[size][size];
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

    public void initTiles(){
        int x, y;
        int figureWidth = this.getWidth() / size;
        int figureHeight = this.getHeight() / size;
        ArrayList<Tile> tiles = controller.getTiles();
        for (int i = 0; i < tiles.size(); i++){
            Tile tile = tiles.get(i);
            x = tile.getPosition().getX() % size;
            y = tile.getPosition().getY() % size;
            tilesBoard[x][y] = new Figure(controller, x * figureWidth, y * figureHeight, tile.getGoalValue(), figureWidth);
            panels[x][y].add(tilesBoard[x][y]);
            figureForPanel.put(panels[x][y], tilesBoard[x][y]);
        }
        for (int i = 0; i < size; i++){
            for (int j = 0; j < size; j++){
                GridBagConstraints constraints = new GridBagConstraints();
                constraints.gridx = i;
                constraints.gridy = j;
                this.add(panels[i][j], constraints);
            }
        }
        this.revalidate();
        this.repaint();
    }

    public void updateFigures(ArrayList<Tile> tiles) {
        Iterator it = tiles.iterator();
        while (it.hasNext()){
            Tile tile = (Tile) it.next();
            int xPos = tile.getPosition().getX();
            int yPos = tile.getPosition().getY();
            Figure fig = figureForPanel.get(panels[xPos][yPos]);
            if (tile.getGoalValue() != 0){
                fig.setText(String.valueOf(tile.getGoalValue()));
                fig.setOpaque(true);
                fig.setBackground(Constants.BG_COLOR);
            } else {
                fig.setText("");
                fig.setOpaque(false);
                fig.setBackground(Constants.FG_COLOR);
            }
            fig.setForeground(Constants.FG_COLOR);
        }
        this.revalidate();
        this.repaint();
    }



    public void moveBlankTile(Position[] positions) {
        Rectangle from = new Rectangle(positions[1].getX() * Constants.TILE_SIZE,
                positions[1].getY() * Constants.TILE_SIZE,
                Constants.TILE_SIZE,
                Constants.TILE_SIZE);
        Rectangle to = new Rectangle(positions[0].getX() * Constants.TILE_SIZE,
                positions[0].getY() * Constants.TILE_SIZE,
                Constants.TILE_SIZE,
                Constants.TILE_SIZE);
        AnimatePanel animate = new AnimatePanel(panels[positions[1].getX()][positions[1].getY()], from, to);
        animate.start();
    }

    public void updateTilePos(Position[] positions) {
        Position blankTilePos = positions[0];
        Position swappedTilePos = positions[1];

        Figure blankTile = tilesBoard[blankTilePos.getX()][blankTilePos.getY()];
        Figure swappedTile = tilesBoard[swappedTilePos.getX()][swappedTilePos.getY()];

        int xAux = blankTile.getxPos();
        int yAux = blankTile.getyPos();

        blankTile.setxPos(swappedTile.getxPos());
        blankTile.setyPos(swappedTile.getyPos());

        swappedTile.setxPos(xAux);
        swappedTile.setyPos(yAux);

        tilesBoard[swappedTilePos.getX()][swappedTilePos.getY()] = blankTile;
        tilesBoard[blankTilePos.getX()][blankTilePos.getY()] = swappedTile;

        panels[swappedTilePos.getX()][swappedTilePos.getY()] = getPanelForFigure(blankTile);
        panels[blankTilePos.getX()][blankTilePos.getY()] = getPanelForFigure(swappedTile);
    }

    private JPanel getPanelForFigure(Figure fig){
        for (Map.Entry<JPanel, Figure> entry : figureForPanel.entrySet()) {
            if (fig == (entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
