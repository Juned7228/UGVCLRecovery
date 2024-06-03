package com.bytecodesolutions.ugvclrecovery;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.bytecodesolutions.ugvclrecovery.model.Consumer;


import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class CSVImporter {

    public static void importCSV(Context context, Uri uri) throws Exception {
        DatabaseHelper dbHelper = new DatabaseHelper(context);


       // BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        dbHelper.deleteAllDataFromTable();
        FileInputStream fis=null;
        Workbook workbook=null;
        try {
            // Read Excel Data using Apache POI
           fis =(FileInputStream) context.getContentResolver().openInputStream(uri);
          // context.getContentResolver().openInputStream(uri);
           workbook = WorkbookFactory.create(fis);

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {

                DataFormatter df = new DataFormatter();
                df.addFormat("General", new java.text.DecimalFormat("#.###############"));
                if(row.getRowNum()>0){
                    if(row.getCell(6).getCellType()==Cell.CELL_TYPE_NUMERIC &&
                            (df.formatCellValue(row.getCell(7)).length()==10 || df.formatCellValue(row.getCell(7)).length()==0)){

                        System.out.println(row.getCell(0)==null?"":row.getCell(0).toString());
                        System.out.println(row.getCell(7)==null?"":df.formatCellValue(row.getCell(7)));

                        Consumer consumer=new Consumer(
                                row.getCell(0)==null?"": df.formatCellValue( row.getCell(0)),
                                row.getCell(1)==null?"":row.getCell(1).toString().toUpperCase(),
                                row.getCell(2)==null?"":row.getCell(2).toString().toUpperCase(),
                                row.getCell(3)==null?"":row.getCell(3).toString().toUpperCase(),
                                row.getCell(4)==null?"":row.getCell(4).toString().toUpperCase(),
                                row.getCell(5)==null?"":row.getCell(5).toString().toUpperCase(),
                                row.getCell(6)==null?"":row.getCell(6).toString().toUpperCase(),
                                row.getCell(7)==null?"":df.formatCellValue(row.getCell(7)),
                                row.getCell(8)==null?"":row.getCell(8).toString().toUpperCase(),
                                row.getCell(9)==null?"":row.getCell(9).toString().toUpperCase()

                        );
                        dbHelper.insertData(consumer);

                    }
                    else{
                        System.out.println("c1 "+ row.getCell(6).getCellType());
                        System.out.println("c2 "+ df.formatCellValue(row.getCell(7)));
                        System.out.println("c3 "+ df.formatCellValue( row.getCell(0)));
                        throw new Exception();
                    }
                }





            }




        }catch (IOException e){
                e.printStackTrace();

        }finally {
            // Close resources
            try {
                fis.close();
            }catch (IOException e) {
                e.printStackTrace();
            }


        }

       /* try {



            String line;
            reader.readLine();
            while ((line = reader.readLine()) != null) {
                // Split the CSV line into values

                String[] values = line.split("\t");
                String remarks;
                if(values.length==10){
                    remarks=values[9];
                }
                else {
                    remarks="";
                }
                // Insert the values into your database table
                // Example: db.execSQL("INSERT INTO your_table_name (column1, column2, ...) VALUES (?, ?, ...)", values);
                if(values.length<=10){
                    Consumer consumer=new Consumer(
                            values[0].toUpperCase(),
                            values[1].toUpperCase(),
                            values[2].toUpperCase(),
                            values[3].toUpperCase(),
                            values[4].toUpperCase(),
                            values[5].toUpperCase(),
                            values[6].toUpperCase(),
                            values[7].toUpperCase(),
                            values[8].toUpperCase(),
                            remarks
                    );



                  //  Toast.makeText(context,values[0],Toast.LENGTH_LONG).show();
                }
                else{
                    throw new IOException();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }*/


    }

    public static void ExporttoExcel(Context context,List<Consumer> consumerList){
        //File exportDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File exportDir = context.getExternalFilesDir(null);
       // File exportDir =context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
       System.out.println("Export"+consumerList.size());
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, "recovery.xlsx");

        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Data");
            // Replace this with your actual data
            Iterator<Consumer> itr=consumerList.iterator();
            int row=0;
            int c=0;
            Row rw=sheet.createRow(row);
            rw.createCell(c++).setCellValue("cons no");
            rw.createCell(c++).setCellValue("name");
            rw.createCell(c++).setCellValue("address1");
            rw.createCell(c++).setCellValue("tarif");
            rw.createCell(c++).setCellValue("meter no");
            rw.createCell(c++).setCellValue("rootcode");
            rw.createCell(c++).setCellValue("amount");
            rw.createCell(c++).setCellValue("mobile");
            rw.createCell(c++).setCellValue("consumertype");
            rw.createCell(c++).setCellValue("remarks");

            row++;
            while(itr.hasNext()){
                Consumer consumer=itr.next();
                Row excelRow = sheet.createRow(row);
                int col=0;
                Cell cell;
                cell = excelRow.createCell(col++);
                cell.setCellValue(consumer.getNum());
                cell = excelRow.createCell(col++);
                cell.setCellValue(consumer.getName());
                cell = excelRow.createCell(col++);
                cell.setCellValue(consumer.getAddress1());
                cell = excelRow.createCell(col++);
                cell.setCellValue(consumer.getTarif());
                cell = excelRow.createCell(col++);
                cell.setCellValue(""+consumer.getMeterno());
                cell = excelRow.createCell(col++);
                cell.setCellValue(""+consumer.getRootcode());
                cell = excelRow.createCell(col++);
                cell.setCellValue(consumer.getAmount()+0);
                cell = excelRow.createCell(col++);
                cell.setCellValue(consumer.getMobile());
                cell = excelRow.createCell(col++);
                cell.setCellValue(consumer.getType());
                cell = excelRow.createCell(col++);
                cell.setCellValue(consumer.getRemarks());
                row++;
            }


            FileOutputStream fos = new FileOutputStream(file);
            workbook.write(fos);
            fos.close();



        }catch (Exception e){
            e.printStackTrace();
        }
    }


}

