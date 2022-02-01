/*
 * Copyright (c) 13/3/18 4:51 PM Bitwise Ventures
 * Author : Anand Panchal
 * Author Email : anand4686@gmail.com
 */

package com.gradle.util.export;

import com.opencsv.CSVWriter;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;
import com.opencsv.exceptions.CsvException;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.List;


public class WriteCsvToResponse<E> {

    private static final Logger LOGGER = Logger.getLogger(WriteCsvToResponse.class);

    public void writeCsv(HttpServletResponse response, List<E> objList, String[] columns)  throws IOException{

        try {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment;filename=CSV_" + LocalDateTime.now() + ".csv");
            PrintWriter writer = response.getWriter();
            ColumnPositionMappingStrategy mapStrategy
                    = new ColumnPositionMappingStrategy();

            mapStrategy.setType(objList.get(0).getClass());

            mapStrategy.generateHeader();



            mapStrategy.setColumnMapping(columns);





            StatefulBeanToCsv btcsv = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
                    //.withMappingStrategy(mapStrategy)
                    .withSeparator(',')
                    .build();



            btcsv.write(objList);


        } catch (CsvException ex) {

            LOGGER.error("Error mapping Bean to CSV", ex);
        }
    }


    public void writeCsvStringArray(HttpServletResponse response, List<String[]> objList, String[] columns)  throws IOException{

        try {
            response.setContentType("text/csv");
            response.setHeader("Content-Disposition", "attachment;filename=CSV_" + LocalDateTime.now() + ".csv");
            PrintWriter writer = response.getWriter();
            ColumnPositionMappingStrategy mapStrategy
                    = new ColumnPositionMappingStrategy();
            mapStrategy.setType(objList.get(0).getClass());
            mapStrategy.generateHeader();
            mapStrategy.setColumnMapping(columns);
            StatefulBeanToCsv btcsv = new StatefulBeanToCsvBuilder(writer)
                    .withQuotechar(CSVWriter.DEFAULT_ESCAPE_CHARACTER)
//                    .withMappingStrategy(mapStrategy)
                    .withSeparator(',')

                    .build();
            btcsv.write(objList);
        } catch (CsvException ex) {
            LOGGER.error("Error mapping Bean to CSV", ex);
        }
    }

}
