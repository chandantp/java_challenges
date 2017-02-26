package org.chandan.forex.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.chandan.forex.model.ExchangeRate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateDAO {
	
	@SuppressWarnings("unused")
	private DataSource dataSource;
	private NamedParameterJdbcTemplate  jdbcTemplate;	
	
	static class XchangeRateMapper implements RowMapper<ExchangeRate> {
		public ExchangeRate mapRow(ResultSet rs, int rowNum) throws SQLException {
			ExchangeRate rate = new ExchangeRate();
			rate.setCode(rs.getString("code"));
			rate.setRate(rs.getFloat("per_unit_usd_rate"));
			return rate;
		}
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	    this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public List<ExchangeRate> listExchangeRates() {
		String sql = "SELECT * FROM xrates";
	    List<ExchangeRate> xrates = jdbcTemplate.query(sql, new XchangeRateMapper());
	    return xrates;
	}
	
	public List<ExchangeRate> listExchangeRates(String quote) {
		String sql = "SELECT * FROM xrates WHERE code = :code";
		SqlParameterSource namedParameters = new MapSqlParameterSource("code", quote);
	    List<ExchangeRate> xrates = jdbcTemplate.query(sql, namedParameters, new XchangeRateMapper());
	    return xrates;
	}
}
