package de.htwberlin.jdbc;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
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
    if(trayExists(trayId)) {
      List<Integer> sampleList = getSampleFromTray(trayId);
      List<String> tablesToModify = List.of("place", "sample");

      for(Integer sampleId : sampleList) {
        for(String fromTable : tablesToModify) {
          String sql = String.join("", "delete from ", fromTable ," where sampleId = ?");
          try(PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, sampleId);
            stmt.executeUpdate();
          } catch (SQLException e) {
            L.error("", e);
            throw new CoolingSystemException(e);
          }
        }
      }
    } else {
      throw new CoolingSystemException("TrayId existiert nicht.");
    }
  }

  private List<Integer> getSampleFromTray(Integer trayId) {
    List<Integer> sampleList = new ArrayList<>();

    String sql = String.join(" ", "select sample.sampleId",
                                        "from sample join place on sample.sampleId = place.sampleId",
                                        "join tray on place.trayId = tray.trayId",
                                        "where tray.trayId=?");
    try(PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, trayId);
      try(ResultSet rs = stmt.executeQuery()) {
        while(rs.next()) {
          sampleList.add(rs.getInt("sampleId"));
        }
      }
    } catch (SQLException e) {
      L.error("", e);
      throw new DataException(e);
    }
    return sampleList;
  }

  private boolean trayExists(Integer trayId) {
    String sql = "select trayId from tray where trayId =?";
    try(PreparedStatement stmt = connection.prepareStatement(sql)) {
      stmt.setInt(1, trayId);
      try(ResultSet rs = stmt.executeQuery()) {
        if(rs.next()) {
          return true;
        } else {
          return false;
        }
      }
    } catch (SQLException e) {
      L.error("", e);
      throw  new DataException(e);
    }
  }
}
