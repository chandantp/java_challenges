package org.chandan.forex.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.chandan.forex.model.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

@Component
public class CurrencyDAO {
	
	@SuppressWarnings("unused")
	private DataSource dataSource;
	private NamedParameterJdbcTemplate jdbcTemplate;	
	
	static class CurrencyMapper implements RowMapper<Currency> {
		public Currency mapRow(ResultSet rs, int rowNum) throws SQLException {
			Currency currency = new Currency();
			currency.setCode(rs.getString("code"));
			currency.setDisplayName(rs.getString("display_name"));
			return currency;
		}
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	    this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	public List<Currency> listCurrencies() {
		String sql = "select * from currencies";
	    List<Currency> currencies = jdbcTemplate.query(sql, new CurrencyMapper());
	    return currencies;
	}
	
	public Currency getCurrency(String quote) {
		String sql = "SELECT * FROM currencies WHERE code = :code";
		SqlParameterSource namedParameters = new MapSqlParameterSource("code", quote);
	    List<Currency> currencies = jdbcTemplate.query(sql, namedParameters, new CurrencyMapper());
	    return ((currencies != null && currencies.size() > 0) ? currencies.get(0) : null);
	}
}