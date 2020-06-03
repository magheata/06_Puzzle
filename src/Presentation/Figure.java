/* Created by andreea on 02/06/2020 */
package Presentation;

import Application.Controller;
import Config.Constants;
import Domain.Position;
import Domain.ShiftDirection;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.border.LineBorder;

public class Figure extends JButton implements ActionListener{

    private int xPos, yPos;
    private int value;
    private int dimension;

    public void setImage(Image image) {
        if (image != null){
            this.setIcon(new ImageIcon(image));
        } else {
            this.setIcon(null);
        }
    }

    private Controller controller;

    public Figure(int xSolPos, int ySolPos, ImageIcon subimage, int dimension){
        this.dimension = dimension;

        xPos = xSolPos;
        yPos = ySolPos;

        this.setIcon(subimage);
        this.setPreferredSize(new Dimension(subimage.getIconWidth(), subimage.getIconHeight()));
        this.addActionListener(this);
    }

    public Figure(Controller controller, int xSolPos, int ySolPos, int value, int dimension){
        this.dimension = dimension;
        this.value = value;
        this.controller = controller;
        xPos = xSolPos;
        yPos = ySolPos;

        this.setSize(new Dimension(dimension, dimension));
        this.setMinimumSize(new Dimension(dimension, dimension));
        this.setPreferredSize(new Dimension(dimension, dimension));
        this.setFont(new Font("Sans", Font.BOLD, 90));
        this.setBorder(new LineBorder(Color.black, 1));
        if (value != 0){
            this.setOpaque(true);
            this.setBackground(Constants.BG_COLOR);
            this.setForeground(Constants.FG_COLOR);
            this.setText(String.valueOf(value));
        }
        this.addActionListener(this);
    }

    public Figure(Controller controller, int xSolPos, int ySolPos, Image image, int value, int dimension){
        this.dimension = dimension;
        this.value = value;
        this.controller = controller;
        xPos = xSolPos;
        yPos = ySolPos;
        this.setSize(new Dimension(dimension, dimension));
        this.setMinimumSize(new Dimension(dimension, dimension));
        this.setPreferredSize(new Dimension(dimension, dimension));

        if (value != 0){
            this.setIcon(new ImageIcon(image));
        }
        this.addActionListener(this);
    }

    public int getxPos() {
        return xPos;
    }

    public void setxPos(int xPos) {
        this.xPos = xPos;
    }

    public int getyPos() {
        return yPos;
    }

    public void setyPos(int yPos) {
        this.yPos = yPos;
    }

    @Override
    public String toString() {
        return "{"  + value +
                "} ";
    }

    @Override
    public void actionPerformed(ActionEvent arg0) {
        Object[] result = controller.checkIfCanMove(xPos / Constants.TILE_SIZE, yPos / Constants.TILE_SIZE);
        if (result != null){
            controller.swapTiles((ShiftDirection) result[0], (Position[]) result[1]);
        }
    }
}