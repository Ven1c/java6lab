/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package my.integraleditor;

import java.io.Serializable;

/**
 *
 * @author Ilya
 */
public class RecIntegral implements Serializable, Runnable {
   private double min_value = 0;
   private double max_value = 0;
   private double step = 0;
   private double result = 0;

    public RecIntegral(double min_value, double max_value, double step, double result)throws InvalidValueException {
        
        if (min_value < 0.000001 || min_value > 1000000) {
            throw new InvalidValueException("Некорректное значение. Диапазон: от 0,000001 до 1000000.");
        }
        if (max_value < 0.000001 || max_value > 1000000) {
            throw new InvalidValueException("Некорректное значение. Диапазон: от 0,000001 до 1000000.");
        }
        if (step < 0.000001 || step > 1000000) {
            throw new InvalidValueException("Некорректное значение. Диапазон: от 0,000001 до 1000000.");
        }
        if (step < 0.000001 || step > 1000000) {
            throw new InvalidValueException("Некорректное значение. Диапазон: от 0,000001 до 1000000.");
        }
        
        this.min_value = min_value;
        this.max_value = max_value;
        this.step = step;
        this.result = result;
    }
    
     
    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        integral();
        long endTime = System.currentTimeMillis();
        System.out.printf("Result: %f Time:%d\n", result,(endTime-startTime));
    }
    
    public double integral(){
        double value = min_value;
        double next_value = min_value;
        double sqrt;
        double new_sqrt;
        result = 0;
        int max_i= (int)((max_value - min_value)/step);
        for (int i=1;i<=max_i+1;i++){
            next_value += step;
            if(next_value > max_value){
                next_value = max_value;
            }
            new_sqrt = Math.sqrt(next_value);
            sqrt=Math.sqrt(value);
            
            result += ((new_sqrt+sqrt)*(next_value-value))/2;
            
            value=next_value;
            
        }
        
        return result;
       
   }
     public double getMinValue() {
        return min_value;
    }

    public void setMinValue(double minValue) {
        this.min_value = minValue;
    }

    public double getMaxValue() {
        return max_value;
    }

    public void setMaxValue(double maxValue) {
        this.max_value = maxValue;
    }

    public double getStep() {
        return step;
    }

    public void setStep(double step) {
        this.step = step;
    }

    public double getResult() {
        return result;
    }

    public void setResult(double result) {
        this.result = result;
    }
}
