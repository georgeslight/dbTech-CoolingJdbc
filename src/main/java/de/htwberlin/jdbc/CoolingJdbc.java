package de.htwberlin.jdbc;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import de.htwberlin.exceptions.CoolingSystemException;
import org.dbunit.DatabaseUnitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.htwberlin.domain.Sample;
import de.htwberlin.exceptions.DataException;

public class CoolingJdbc implements ICoolingJdbc {

  private static final Logger L = LoggerFactory.getLogger(CoolingJdbc.class);
  private Connection connection;

  @Override
  public void setConnection(Connection connection) {
    this.connection = connection;
  }

  @SuppressWarnings("unused")
  private Connection useConnection() {
    if (connection == null) {
      throw new DataException("Connection not set");
    }
    return connection;
  }

  @Override
  public List<String> getSampleKinds() {
    L.info("getSampleKinds: start");
    // TODO Auto-generated method stub
    List<String> sampleKindList = new LinkedList<String>();
    String sql = "select text from samplekind order by text asc";
    try (Statement stmt = connection.createStatement()) {
      try(ResultSet rs = stmt.executeQuery(sql)) {
        while(rs.next()) {
          sampleKindList.add(rs.getString("text"));
        }
      }
    } catch(SQLException e) {
      L.error("", e); {
        throw new DataException(e);
      }
    }
    return sampleKindList;
  }

  @Override
  public Sample findSampleById(Integer sampleId) {
    L.info("findSampleById: sampleId: " + sampleId);
    // TODO Auto-generated method stub
    Sample sampleById = null;
    String sql = String.join(" ", "Select *",
            "from sample",
            "where sampleid = ?");
    try(PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1,  sampleId);
      try(ResultSet rs = stmt.executeQuery()) {
        if(rs.next()) {
          int sampleKindId = rs.getInt("sampleId");
          Date expirationDate = rs.getDate("expirationDate");
          LocalDate expirationLocalDate = expirationDate.toLocalDate();
          sampleById = new Sample(sampleId, sampleKindId, expirationLocalDate);
          return sampleById;
        } else {
          throw new CoolingSystemException("sampleId existiert nicht in db: " + sampleId);
        }
      }
    } catch(SQLException e) {
      L.error("", e); {
        throw new DataException(e);
      }
    }
  }

  @Override
  public void createSample(Integer sampleId, Integer sampleKindId) {
    L.info("createSample: sampleId: " + sampleId + ", sampleKindId: " + sampleKindId);
    // TODO Auto-generated method stub
    int validNoOfDays = getValidNoOfDays(sampleKindId);
    Date expirationDate = Date.valueOf(LocalDate.now().plusDays(validNoOfDays));

    String sql = String.join("",
                              "insert into sample",
                                        "(sampleId, sampleKindId, expirationDate)",
                                        " values(?, ?, ?)");
    try(PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, sampleId);
      stmt.setInt(2, sampleKindId);
      stmt.setDate(3, expirationDate);
      stmt.executeUpdate();
    } catch(SQLException e) {
      L.error("", e);
      throw new CoolingSystemException(e);
    }


  }


  private int getValidNoOfDays(Integer sampleKindId) {
    String sqlDays = String.join(" ",
            "select validNoOfDays from sampleKind",
                      "where sampleKind.sampleKindId =?");
    Integer validNoOfDays;
    try(PreparedStatement stmt = connection.prepareStatement(sqlDays)) {
      stmt.setInt(1, sampleKindId);
      try(ResultSet rs = stmt.executeQuery()) {
        if(rs.next()) {
          validNoOfDays = rs.getInt("validNoOfDays");
        } else {
          throw new CoolingSystemException();
        }
      }
    } catch(SQLException e) {
      L.error("", e);
      throw new DataException(e);
    }
    return validNoOfDays;
  }

  @Override
  public void clearTray(Integer trayId) {
    L.info("clearTray: trayId: " + trayId);
    // TODO Auto-generated method stub
    String sql = String.join("",
                            "delete (",
                                    "select sample.sampleId ",
                                    "from tray join place on tray.trayId = place.trayId ",
                                    "join sample on place.sampleId = sample.sampleId ",
                                    "where tray.trayId = ?)");
    try(PreparedStatement stmt = connection.prepareStatement(sql)){
      stmt.setInt(1, trayId);
      int affectedRecords = stmt.executeUpdate();

      if(affectedRecords == 0) {
        throw new CoolingSystemException("trayId existiert nicht in db: " + trayId);
      } else {
        System.out.println("Number of deleted records: " + affectedRecords);
      }
    } catch(SQLException e) {
      L.error("", e);
      throw new CoolingSystemException(e);
    }
  }
}
